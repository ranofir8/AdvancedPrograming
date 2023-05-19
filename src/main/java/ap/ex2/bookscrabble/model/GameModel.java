package ap.ex2.bookscrabble.model;

import java.net.Socket;
import java.util.Observable;

public abstract class GameModel extends Model {
    protected GameInstance gi;
    //protected Socket outSocket; //both have sockets but with different usage
    public GameModel() {

    }

    public void establishConnection() {
    }
}
