package ap.ex2.bookscrabble.view;
import ap.ex2.bookscrabble.R;
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

    private HashMap<String, MediaPlayer> loadedSounds;

    private SoundManager() {
        this.loadedSounds = new HashMap<>();
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
                return new MediaPlayer(loadSoundFile(s));
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
}
