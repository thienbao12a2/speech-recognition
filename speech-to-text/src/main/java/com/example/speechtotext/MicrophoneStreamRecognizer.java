package com.example.speechtotext;

import com.google.api.gax.rpc.ApiStreamObserver;
import com.google.api.gax.rpc.BidiStreamingCallable;
import com.google.cloud.speech.v1.*;
import com.google.protobuf.ByteString;

import javax.sound.sampled.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

public class MicrophoneStreamRecognizer {

    private static final AudioFormat AUDIO_FORMAT = new AudioFormat(16000, 16, 1, true, false);
    private static final int BUFFER_SIZE = 4096;

    private final SpeechClient speechClient;
    private final RecognitionConfig recognitionConfig;
    private final AtomicBoolean keepRecording = new AtomicBoolean(true);
    private TargetDataLine targetDataLine;

    public MicrophoneStreamRecognizer(SpeechClient speechClient, RecognitionConfig recognitionConfig) {
        this.speechClient = speechClient;
        this.recognitionConfig = recognitionConfig;
    }

    public void startRecognition(Consumer<String> onTranscription) throws LineUnavailableException {
        DataLine.Info info = new DataLine.Info(TargetDataLine.class, AUDIO_FORMAT);

        if (!AudioSystem.isLineSupported(info)) {
            throw new LineUnavailableException("The line is not supported");
        }

        targetDataLine = (TargetDataLine) AudioSystem.getLine(info);
        targetDataLine.open(AUDIO_FORMAT);
        targetDataLine.start();

        // Setting up the API stream observer to receive transcription results
        ApiStreamObserver<StreamingRecognizeResponse> responseObserver = new ApiStreamObserver<>() {
            @Override
            public void onNext(StreamingRecognizeResponse response) {
                if (response.getResultsCount() > 0) {
                    final StreamingRecognitionResult result = response.getResultsList().get(0);
                    if (result.getAlternativesCount() > 0) {
                        final String transcript = result.getAlternatives(0).getTranscript();
                        System.out.println("Transcript: " + transcript);
                        onTranscription.accept(transcript);
                    }
                }
            }

            public void onError(Throwable t) {
                t.printStackTrace();
            }

            @Override
            public void onCompleted() {
                System.out.println("Recognition completed.");
            }
        };

        // Start streaming recognition
        BidiStreamingCallable<StreamingRecognizeRequest, StreamingRecognizeResponse> callable = speechClient.streamingRecognizeCallable();
        ApiStreamObserver<StreamingRecognizeRequest> clientStream = callable.bidiStreamingCall(responseObserver);


        // Send initial configuration message
        StreamingRecognitionConfig streamingConfig = StreamingRecognitionConfig.newBuilder()
                .setConfig(recognitionConfig)
                .setInterimResults(true)
                .build();
        StreamingRecognizeRequest initialRequest = StreamingRecognizeRequest.newBuilder()
                .setStreamingConfig(streamingConfig)
                .build();
        clientStream.onNext(initialRequest);

        Thread streamingThread = new Thread(() -> {
            byte[] buffer = new byte[BUFFER_SIZE];
            while (keepRecording.get()) {
                int bytesRead = targetDataLine.read(buffer, 0, buffer.length);
                ByteString data = ByteString.copyFrom(buffer, 0, bytesRead);
                StreamingRecognizeRequest request = StreamingRecognizeRequest.newBuilder()
                        .setAudioContent(data)
                        .build();
                clientStream.onNext(request);
            }
            clientStream.onCompleted();
        });
        streamingThread.start();
    }

    public void stopRecognition() {
        keepRecording.set(false);
        if (targetDataLine != null) {
            targetDataLine.stop();
            targetDataLine.close();
        }
    }
}
