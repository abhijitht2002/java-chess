package com.chess.audio;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import java.io.BufferedInputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import static com.chess.config.Config.CAPTURE_SOUND;
import static com.chess.config.Config.MOVE_SOUND;

public class SoundPlayer {

    private static final Map<String, Clip> soundCache = new HashMap<>();

    // Preload sounds internally
    public static void preloadSounds(){
        soundCache.put("MOVE_SOUND_CACHE", loadClip(MOVE_SOUND));
        soundCache.put("CAPTURE_SOUND_CACHE", loadClip(CAPTURE_SOUND));
        System.out.println("All sounds preloaded.");
    }

    public static void playSound(String soundKey){

        Clip clip = soundCache.get(soundKey);
        if(clip != null){
            clip.setFramePosition(0);   // Reset to start
            clip.start();   // Play the sound
        }
    }

    //  Private
    private static Clip loadClip(String soundFile){

        try{
            InputStream inputStream = SoundPlayer.class.getResourceAsStream(soundFile);
            if(inputStream == null){
                throw new IllegalArgumentException("Sound file not found: " + soundFile);
            }

            BufferedInputStream bufferedInputStream = new BufferedInputStream(inputStream);
            AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(bufferedInputStream);
            Clip clip = AudioSystem.getClip();
            clip.open(audioInputStream);
            return clip;
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

}
