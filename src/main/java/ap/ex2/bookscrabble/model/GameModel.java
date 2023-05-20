package ap.ex2.bookscrabble.model;

import java.net.Socket;
import java.util.Observable;

public abstract class GameModel extends Model {
    protected GameInstance gi;
    //protected Socket outSocket; //both have sockets but with different usage
    public GameModel(String nickname) {
        this.gi = new GameInstance(nickname);
        /*String realNickname = this.gi.setNickname(nickname);
        if(realNickname==nickname){
            int x=0;//todo ?
        }*/
    }

    public abstract int getDisplayPort();

    public abstract void establishConnection() throws Exception;
}
