package ap.ex2.bookscrabble.view;
import ap.ex2.bookscrabble.R;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;

import java.io.FileNotFoundException;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

public class SoundManager {
    public final static String SOUND_TILE_PRESSED = "tileSnap.wav";
    public final static String SOUND_TILE_ADD = "tileAdded.wav";
    public final static String SOUND_TILE_SHUFFLE = "shuffleTiles.wav";
    public final static String SOUND_PLAYER_JOINED = "playerJoined.wav";
    public final static String SOUND_STARTING_GAME = "startGame3.wav";
    public final static String SOUND_OF_FAILURE = "soundOfFailure.mp3";
    public final static String SOUND_NEW_WORD = "newWord.wav";
    public final static String SOUND_POP = "pop.wav";
    public final static String SOUND_YOUR_TURN = "yourTurn.wav";
    public static final String SOUND_OF_APPROVAL = "soundOfApproval.wav";
    public static final String SOUND_OF_REJECTED = "soundOfRejected.wav";
    public static final String SOUND_OF_WIN = "soundOfWin.mp3";


    public final static SoundManager singleton = new SoundManager();


    private final HashMap<String, MediaPlayer> loadedSounds;
    private final DoubleProperty masterVolume;
    private List<MediaPlayer> playQueue;

    private boolean isQueuePlaying = false;

    private SoundManager() {
        this.loadedSounds = new HashMap<>();
        this.masterVolume = new SimpleDoubleProperty();
        this.playQueue = new LinkedList<>();
    }

    public void enablePopSound() {
        this.masterVolume.addListener((observableValue, number, t1) -> {
            this.playSound(SOUND_POP, true);
        });
    }

    private Media loadSoundFile(String fileName) throws FileNotFoundException {
        try {
            return new Media(R.getResource("sounds/" + fileName).toURI().toString());
        } catch (URISyntaxException e) {
            throw new FileNotFoundException();
        }
    }

    public void playSound(String s, boolean ignoreOthers) {
        synchronized (this) {
            SoundManager that = this;
            this.loadedSounds.computeIfAbsent(s, s1 -> {
                try {
                    MediaPlayer mp = new MediaPlayer(loadSoundFile(s));
                    mp.volumeProperty().bind(masterVolume);
                    if (!ignoreOthers)
                        mp.setOnEndOfMedia(that::playFromQueue);
                    return mp;
                } catch (FileNotFoundException e) {
                    throw new RuntimeException(e);
                }
            });
        }

        MediaPlayer mp = this.loadedSounds.get(s);
        if (mp == null) {
            System.out.println("could not load sound");
            return;
        }

        if (ignoreOthers) {
            this.playMediaPlayer(mp);
        } else {
            if (this.isQueuePlaying) {
                this.playQueue.add(mp);
            } else {
                this.isQueuePlaying = true;
                this.playMediaPlayer(mp);
            }
        }
    }

    public void playSound(String s) {
        this.playSound(s, false);
    }

    private void playMediaPlayer(MediaPlayer mp) {
        mp.seek(Duration.millis(0));
        mp.play();
    }

    private synchronized void playFromQueue() {
        if (this.playQueue.isEmpty())
            this.isQueuePlaying = false;
        else
            this.playMediaPlayer(this.playQueue.remove(0));
    }

    // value between 0.0 and 1.0
    public void bindMasterVolumeTo(DoubleProperty dp) {
        this.masterVolume.bind(dp);
    }

    public static void main(String[] args) {

    }
}
