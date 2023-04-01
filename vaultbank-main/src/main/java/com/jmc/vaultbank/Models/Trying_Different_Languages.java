package com.jmc.vaultbank.Models;

import java.io.*;
import okhttp3.*;
import org.json.JSONObject;
import javazoom.jl.decoder.JavaLayerException;
import javazoom.jl.player.advanced.AdvancedPlayer;
import javazoom.jl.player.advanced.PlaybackEvent;
import javazoom.jl.player.advanced.PlaybackListener;

public class Trying_Different_Languages {
    private static final String API_KEY = "AIzaSyDdz0JqhhfxrKWh2FRIIuEXlgk3rBE2TEw";
    private static final String API_URL = "https://texttospeech.googleapis.com/v1/text:synthesize?key=" + API_KEY;
    private final OkHttpClient httpClient;

    public Trying_Different_Languages() {
        httpClient = new OkHttpClient();
    }

    public void speak(String text) {
        String requestBodyJson = "{" +
                "\"input\": {\"text\": \"" + text + "\"}," +
                "\"voice\": {\"languageCode\": \"en-US\", \"ssmlGender\": \"FEMALE\"}," +
                "\"audioConfig\": {\"audioEncoding\": \"MP3\"}" +
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
            String audioContent = jsonResponse.getString("audioContent");
            byte[] audioBytes = java.util.Base64.getDecoder().decode(audioContent);

            try (InputStream inputStream = new ByteArrayInputStream(audioBytes)) {
                AdvancedPlayer player = new AdvancedPlayer(inputStream);
                player.setPlayBackListener(new PlaybackListener() {
                    @Override
                    public void playbackFinished(PlaybackEvent event) {
                        player.close();
                    }
                });
                player.play();
            } catch (IOException | JavaLayerException e) {
                e.printStackTrace();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
