package ap.ex2.bookscrabble.model;

import ap.ex2.scrabble.Player;

import java.net.Socket;
import java.util.Observable;

public abstract class GameModel extends Model {
    protected Player myPlayer;
    protected Socket outSocket;
    @Override
    public abstract void update(Observable o, Object arg);
}
