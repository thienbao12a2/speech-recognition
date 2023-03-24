package com.example.speechtotext;

import com.google.cloud.speech.v1.RecognitionConfig;
import com.google.cloud.speech.v1.SpeechClient;
import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import javax.sound.sampled.LineUnavailableException;
import java.io.IOException;

public class HelloApplication extends Application {

    private MicrophoneStreamRecognizer recognizer;

    @Override
    public void start(Stage primaryStage) throws Exception {
        SpeechClient speechClient = SpeechClient.create();

        RecognitionConfig recognitionConfig = RecognitionConfig.newBuilder()
                .setEncoding(RecognitionConfig.AudioEncoding.LINEAR16)
                .setSampleRateHertz(16000)
                .setLanguageCode("en-US")
                .build();

        recognizer = new MicrophoneStreamRecognizer(speechClient, recognitionConfig);

        TextArea transcriptionArea = new TextArea();
        transcriptionArea.setEditable(false);

        Button recordButton = new Button("Start Recording");
        recordButton.setOnAction(event -> {
            if ("Start Recording".equals(recordButton.getText())) {
                try {
                    recognizer.startRecognition(transcriptionArea::appendText);
                    recordButton.setText("Stop Recording");
                } catch (LineUnavailableException e) {
                    e.printStackTrace();
                }
            } else {
                recognizer.stopRecognition();
                recordButton.setText("Start Recording");
            }
        });

        VBox vbox = new VBox(10, transcriptionArea, recordButton);
        vbox.setAlignment(Pos.CENTER);
        vbox.setFillWidth(true);

        primaryStage.setScene(new Scene(vbox, 600, 400));
        primaryStage.setTitle("Voice Recognition");
        primaryStage.show();
    }

    public static void main(String[] args) throws IOException {
        Trying_Different_Languages I = new Trying_Different_Languages();
        I.speak("Hello World");
        launch(args);
    }
}