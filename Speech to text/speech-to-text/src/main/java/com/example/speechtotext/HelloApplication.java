package com.example.speechtotext;
import javax.sound.sampled.LineUnavailableException;
import java.io.IOException;

public class HelloApplication{

    public static void main(String[] args) throws IOException, LineUnavailableException, InterruptedException {
        Trying_Different_Languages I = new Trying_Different_Languages();
        I.speak("Hello World");
        MicrophoneStreamRecognizer We = new MicrophoneStreamRecognizer();
        We.listen(10);
    }
}