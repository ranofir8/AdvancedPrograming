package ap.ex2.bookscrabble.model.guest;

import ap.ex2.bookscrabble.common.Command;
import ap.ex2.bookscrabble.common.Command2VM;
import ap.ex2.bookscrabble.common.Protocol;
import ap.ex2.bookscrabble.model.GameModel;
import ap.ex2.bookscrabble.model.MyClientHandler;

import java.io.IOException;
import java.net.Socket;
import java.util.Observable;
import java.util.Observer;

public class GuestGameModel extends GameModel implements Observer {
    private String hostIP;
    private int hostPort;

    private MyClientHandler myHandler;


    public GuestGameModel(String nickname, String hostIP, int hostPort) {
        super(nickname);
        this.hostIP = hostIP;
        this.hostPort = hostPort;
    }

    @Override
    public int getDisplayPort() {
        return this.hostPort;
    }

    @Override
    public void establishConnection() throws Exception {
        System.out.println("GUEST");
        Socket mySocket = new Socket(this.hostIP, this.hostPort);
        this.myHandler = new MyClientHandler(mySocket);
        this.myHandler.addObserver(this);
        this.myHandler.startHandlingClient();
        this.myHandler.sendMsg(Protocol.GUEST_LOGIN_REQUEST + this.gi.getNickname());
    }

    @Override
    public void closeConnection() {
        this.myHandler.close();
    }

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

    protected void finalize() {
        this.myHandler.close();
    }


}
