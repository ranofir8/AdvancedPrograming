package ap.ex2.bookscrabble.model;

import java.net.Socket;
import java.util.Observable;

public abstract class GameModel extends Model {
    protected PlayerStatus myPlayer;
    protected Socket outSocket;
    @Override
    public abstract void update(Observable o, Object arg);
}
