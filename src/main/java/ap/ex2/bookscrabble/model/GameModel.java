package ap.ex2.bookscrabble.model;

import ap.ex2.bookscrabble.common.Command;
import ap.ex2.bookscrabble.common.Command2VM;
import ap.ex2.bookscrabble.common.Protocol;

import java.net.Socket;
import java.util.Observable;
import java.util.Observer;

public abstract class GameModel extends Model implements Observer {
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

    protected abstract void closeConnection();

    @Override
    public void update(Observable o, Object arg) {
        String sentMsg = (String) arg;
        char msgProtocol = sentMsg.charAt(0);
        String msgExtra = sentMsg.substring(1, sentMsg.length());

        switch (msgProtocol) {
            case Protocol.HOST_LOGIN_REJECT:
                setChanged();
                this.notifyObservers(new String[]{"MSG", "This nickname is already taken! Copy-Cat..."});
                this.closeConnection();

                break;

            case Protocol.HOST_LOGIN_ACCEPT:
                setChanged();
                notifyObservers(new Command2VM(Command.GO_TO_GAME_SCENE));
                setChanged();
                notifyObservers(new Command2VM(Command.DISPLAY_PORT, this.getDisplayPort()));
                break;

            default:
                System.err.println("Unknown protocol message!");
                break;
        }
    }
}
