package com.example.speechtotext;

import com.google.cloud.texttospeech.v1.*;
import com.google.protobuf.ByteString;

import java.io.*;
import javazoom.jl.decoder.JavaLayerException;
import javazoom.jl.player.advanced.AdvancedPlayer;
import javazoom.jl.player.advanced.PlaybackEvent;
import javazoom.jl.player.advanced.PlaybackListener;
public class Trying_Different_Languages {

    private final TextToSpeechClient textToSpeechClient;

    public Trying_Different_Languages() throws IOException {
        textToSpeechClient = TextToSpeechClient.create();
    }

    public void speak(String text) {
        System.out.println(text);

        SynthesisInput input = SynthesisInput.newBuilder()
                .setText(text)
                .build();

        VoiceSelectionParams voice = VoiceSelectionParams.newBuilder()
                .setLanguageCode("en-US")
                .setSsmlGender(SsmlVoiceGender.FEMALE)
                .build();

        AudioConfig audioConfig = AudioConfig.newBuilder()
                .setAudioEncoding(AudioEncoding.MP3)
                .build();

        SynthesizeSpeechResponse response = textToSpeechClient.synthesizeSpeech(input, voice, audioConfig);
        ByteString audioContents = response.getAudioContent();

        try (InputStream inputStream = new ByteArrayInputStream(audioContents.toByteArray())) {
            AdvancedPlayer player = new AdvancedPlayer(inputStream);
            player.setPlayBackListener(new PlaybackListener() {
                @Override
                public void playbackFinished(PlaybackEvent event) {
                    player.close();
                }
            });
            player.play();
            System.out.println("Successfully got back synthesizer data");
        } catch (IOException | JavaLayerException e) {
            e.printStackTrace();
        }
    }
}