package com.jmc.vaultbank;

import com.jmc.vaultbank.Models.MicrophoneStreamRecognizer;
import com.jmc.vaultbank.Models.Model;
import com.jmc.vaultbank.Models.Trying_Different_Languages;
import javafx.application.Application;
import javafx.stage.Stage;

import javax.sound.sampled.LineUnavailableException;
import java.io.IOException;


public class App extends Application {
    @Override
    public void start(Stage stage) throws LineUnavailableException, IOException, InterruptedException {
        Model.getInstance().getViewFactory().showLoginWindow();
        Trying_Different_Languages we = new Trying_Different_Languages();
        we.speak("Hello");
        MicrophoneStreamRecognizer I = new MicrophoneStreamRecognizer();
        System.out.println(I.recordAndTranscribe(5));
    }
}
