package com.jmc.vaultbank.Models;

import javax.sound.sampled.*;
import okhttp3.*;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Base64;

public class MicrophoneStreamRecognizer {

    private static final AudioFormat AUDIO_FORMAT = new AudioFormat(16000, 16, 1, true, false);
    private static final int BUFFER_SIZE = 4096;
    private static final String API_KEY = "AIzaSyDdz0JqhhfxrKWh2FRIIuEXlgk3rBE2TEw";
    private static final String API_URL = "https://speech.googleapis.com/v1/speech:recognize?key=" + API_KEY;

    private final OkHttpClient httpClient;

    public MicrophoneStreamRecognizer() {
        this.httpClient = new OkHttpClient();
    }

    public Object recordAndTranscribe(int recordTimeInSeconds) throws LineUnavailableException, InterruptedException, IOException {
        DataLine.Info info = new DataLine.Info(TargetDataLine.class, AUDIO_FORMAT);
        TargetDataLine microphone = (TargetDataLine) AudioSystem.getLine(info);

        microphone.open(AUDIO_FORMAT);
        microphone.start();

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ByteBuffer buffer = ByteBuffer.allocate(BUFFER_SIZE);
        long endTime = System.currentTimeMillis() + recordTimeInSeconds * 1000L;

        Thread captureThread = new Thread(() -> {
            while (System.currentTimeMillis() < endTime) {
                int numBytesRead = microphone.read(buffer.array(), 0, buffer.capacity());
                if (numBytesRead > 0) {
                    out.write(buffer.array(), 0, numBytesRead);
                }
            }
            microphone.stop();
            microphone.close();
        });
        captureThread.start();
        captureThread.join();

        byte[] audioBytes = out.toByteArray();
        out.close(); // Close the ByteArrayOutputStream

        try {
            return (transcribe(audioBytes));
        } catch (IOException e) {
            System.err.println("Error transcribing audio: " + e.getMessage());
        }
        return null;
    }

    private String transcribe(byte[] audioBytes) throws IOException {
        String base64Audio = Base64.getEncoder().encodeToString(audioBytes);

        String requestBodyJson = "{" +
                "\"config\": {" +
                "\"encoding\": \"LINEAR16\"," +
                "\"sampleRateHertz\": 16000," +
                "\"languageCode\": \"en-US\"" +
                "}," +
                "\"audio\": {" +
                "\"content\": \"" + base64Audio + "\"" +
                "}" +
                "}";

        RequestBody requestBody = RequestBody.create(requestBodyJson, MediaType.parse("application/json"));
        Request request = new Request.Builder()
                .url(API_URL)
                .post(requestBody)
                .build();

        try (Response response = httpClient.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("Unexpected code " + response);
            }
            assert response.body() != null;
            String responseBody = response.body().string();
            JSONObject jsonResponse = new JSONObject(responseBody);
            if (jsonResponse.has("results")){
                JSONArray results = jsonResponse.getJSONArray("results");
                JSONObject firstResult = results.getJSONObject(0);
                JSONArray alternatives = firstResult.getJSONArray("alternatives");
                JSONObject firstAlternative = alternatives.getJSONObject(0);

                return firstAlternative.getString("transcript");
            } else {
                return "No transcription results found.";
            }
        }
    }
}