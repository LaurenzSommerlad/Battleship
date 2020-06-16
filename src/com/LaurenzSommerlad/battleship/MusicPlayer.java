package src.com.LaurenzSommerlad.battleship;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.FloatControl;

import java.io.File;
import java.util.ArrayList;

public class MusicPlayer implements Runnable {

    private static Clip clip;
    private ArrayList<String> musicFiles;
    private int currentSongIndex;

    public MusicPlayer(String... files){
        musicFiles = new ArrayList<String>();
        for(String file : files)
            musicFiles.add("GitHub/Battleship/src/com/LaurenzSommerlad/battleship/Music.wav");
            musicFiles.add("GitHub/Battleship/src/com/LaurenzSommerlad/battleship/Battleship Soundtrack.wav");
    }

    private void playSound(String fileName){
        try{
            File soundFile = new File(fileName);
            AudioInputStream ais = AudioSystem.getAudioInputStream(soundFile);
            AudioFormat format = ais.getFormat();
            DataLine.Info info = new DataLine.Info(Clip.class, format);
            clip = (Clip) AudioSystem.getLine(info);
            clip.open(ais);
            setVol(-10);
            clip.start();
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    public static void setVol(double vol){
        FloatControl gainControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
        //float db = (float) (Math.log(vol) / Math.log(10) *20);
        gainControl.setValue((float) vol);
    }

    @Override
    public void run() {
        playSound(musicFiles.get(currentSongIndex));
    }

}

