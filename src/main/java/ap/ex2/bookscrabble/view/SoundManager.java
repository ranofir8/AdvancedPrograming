package ap.ex2.bookscrabble.view;
import ap.ex2.bookscrabble.R;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;

import java.io.File;
import java.io.FileNotFoundException;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.concurrent.Executors;

public class SoundManager {
    public final static String SOUND_TILE_PRESSED = "tileSnap.wav";
    public final static String SOUND_TILE_ADD = "tileAdded.wav";

    public final static SoundManager singleton = new SoundManager();

    private final HashMap<String, MediaPlayer> loadedSounds;
    private final DoubleProperty masterVolume;

    private SoundManager() {
        this.loadedSounds = new HashMap<>();
        this.masterVolume = new SimpleDoubleProperty();
    }

    private Media loadSoundFile(String fileName) throws FileNotFoundException {
        try {
            return new Media(R.getResource(fileName).toURI().toString());
        } catch (URISyntaxException e) {
            throw new FileNotFoundException();
        }
    }

    public void playSound(String s) {
        this.loadedSounds.computeIfAbsent(s, s1 -> {
            try {
                MediaPlayer mp = new MediaPlayer(loadSoundFile(s));
                mp.volumeProperty().bind(masterVolume);
                return mp;
            } catch (FileNotFoundException e) {
                throw new RuntimeException(e);
            }
        });
        MediaPlayer mp = this.loadedSounds.get(s);

        if (mp == null) {
            System.out.println("could not load sound");
            return;
        }
        mp.seek(Duration.millis(0));
        mp.play();
    }

    // value between 0.0 and 1.0
    public void bindMasterVolumeTo(DoubleProperty dp) {
        this.masterVolume.bind(dp);
    }
}
