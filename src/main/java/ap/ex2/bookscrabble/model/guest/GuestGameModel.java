package ap.ex2.bookscrabble.model.guest;

import ap.ex2.bookscrabble.common.Command;
import ap.ex2.bookscrabble.common.Command2VM;
import ap.ex2.bookscrabble.common.Protocol;
import ap.ex2.bookscrabble.model.GameModel;
import ap.ex2.bookscrabble.model.MyClientHandler;
import ap.ex2.bookscrabble.model.host.HostGameModel;
import ap.ex2.bookscrabble.model.host.HostServer;
import ap.ex2.scrabble.Tile;
import ap.ex2.scrabble.Word;

import java.net.Socket;
import java.util.Observable;
import java.util.Observer;


public class GuestGameModel extends GameModel implements Observer {
    private final String hostIP;
    private final int hostPort;

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
        System.out.println("GUEST SOCKET CREATED");
        Socket mySocket = new Socket(this.hostIP, this.hostPort);
        this.myHandler = new MyClientHandler(mySocket);
        this.myHandler.addObserver(this);
        this.myHandler.startHandlingClient();
        this.myHandler.sendMsg(Protocol.GUEST_LOGIN_REQUEST + this.getGameInstance().getNickname());
    }

    @Override
    public void closeConnection() {
        this.myHandler.close();
    }

    @Override
    protected void sendMsgToHost(String msg) {
        this.myHandler.sendMsg(msg);
    }


    @Override
    public void update(Observable o, Object arg) { //updates from myHandler, about incoming events from host
        if (o == this.myHandler) {
            if (arg instanceof String) {
                String sentMsg = (String) arg;
                // nickname will always be null, because only the host can send message to guests
                this.onRecvMessage(null, sentMsg);
            } else if (arg instanceof Integer) {
                int exitCode = (int) arg;
                if (exitCode == MyClientHandler.CLIENT_CONNECTION_CLOSED) {
                    this.onHostCrash();
                    System.out.println("Host closed connection.");
                }
            }
        }
    }

    private void onHostCrash() {
        super.onGameCrash("the host");
    }

    @Override
    protected boolean handleProtocolMsg(String msgSentBy, char msgProtocol, String msgExtra) {
        boolean hasHandled = super.handleProtocolMsg(msgSentBy, msgProtocol, msgExtra);
        if (hasHandled)
            return true;

        // handling specific messages to guest
        switch (msgProtocol) {
            case Protocol.HOST_LOGIN_REJECT_FULL:
                this.notifyViewModel(new String[]{"MSG","The server you're trying to connect is full at the moment, please try later :S"});
                this.closeConnection();
                break;

            case Protocol.HOST_LOGIN_REJECT_NICKNAME:
                this.notifyViewModel(new String[]{"MSG", "This nickname is already taken! Copy-Cat..."});
                this.closeConnection();
                break;

            case Protocol.HOST_LOGIN_ACCEPT:
                notifyViewModel(new Command2VM(Command.GO_TO_GAME_SCENE));
                notifyViewModel(new Command2VM(Command.DISPLAY_PORT, this.getDisplayPort()));
                break;

            case Protocol.PLAYER_UPLOAD:
                // add player to local list
                String newUsername = msgExtra;
                this.onNewPlayer(newUsername);
                break;

            default:
                System.err.println("Unknown protocol message! " + msgProtocol);
                return false;
        }
        return true;
    }

    @Override
    protected Tile _onGotNewTilesHelper(char tileLetter) {
        return this.getGameInstance().getGameBag().getTile(tileLetter);
    }

    @Override
    protected void onBoardUpdateByPlayer(String wordPlaced) {
        Word w = Word.getWordFromNetworkString(wordPlaced, this.getGameInstance().getGameBag()); // not removing tiles from bag
        int expectedScore = this.getGameInstance().getGameBoard().tryPlaceWord(w);
        super.onBoardUpdateByPlayer(wordPlaced);
    }

    protected void finalize() {
        this.myHandler.close();
    }
}
