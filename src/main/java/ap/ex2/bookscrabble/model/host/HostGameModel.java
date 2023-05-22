package ap.ex2.bookscrabble.model.host;

import ap.ex2.BookScrabbleServer.BookScrabbleClient;
import ap.ex2.bookscrabble.common.Command;
import ap.ex2.bookscrabble.common.Command2VM;
import ap.ex2.bookscrabble.common.Protocol;
import ap.ex2.bookscrabble.model.GameModel;
import ap.ex2.bookscrabble.model.PlayerStatus;
import ap.ex2.scrabble.Tile;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;

import java.io.IOException;
import java.util.*;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;

public class HostGameModel extends GameModel implements Observer {
    private BookScrabbleClient myBookScrabbleClient; //for Client
    private int hostPort;
    private HostServer hostServer;
    private Tile.Bag gameBag;
    private List<String> playersTurn;

    public HostGameModel(String nickname, int hostPort, String bookScrabbleSeverIP, int bookScrabbleServerPort) {
        super(nickname); //String configFileName
        this.hostPort = hostPort;

        this.myBookScrabbleClient = new BookScrabbleClient(bookScrabbleSeverIP, bookScrabbleServerPort);
    }

    @Override
    public int getDisplayPort() {
        return this.hostPort;
    }

    @Override
    public void establishConnection() throws IOException {
        System.out.println("HOST");
        // bind to host port
        this.hostServer = new HostServer(this.hostPort, 5, (Thread t, Throwable e) -> {
            System.out.println("Exception in thread: " + t.getName() + "\n"+ e.getMessage());
        });
        this.hostServer.addObserver(this);

        this.hostServer.setMyNickname(this.gi.getNickname());

        this.hostServer.start();
    }

    @Override
    protected void closeConnection() {
        this.hostServer.close();
    }

    @Override
    public void onStartGame() {
        this.gi.onStartGame();  // status PLAYING
//        this. DRAW BOARD todo
        // decide on turns
        // draw tiles for players
        this.hostServer.sendMsgToAllGuests(Protocol.START_GAME + "");

    }

    public void onRecvMessage(String nickname, String msgRecv) {
        // when a guest sends a message
        System.out.println("The player " + nickname+" sent: "+msgRecv);
    }

    public void sendMessageToGuest(String nickname, String msgToSend) {

    }

    protected void finalize() {
        this.hostServer.close();
    }


    @Override
    public void update(Observable o, Object arg) { //updates from:
        if (o == this.hostServer) {
            String[] args = (String[]) arg;
            switch (args[0]) {
                case HostServer.SOCKET_MSG_NOTIFICATION:
                    this.onRecvMessage(args[1], args[2]);
                    break;
                case HostServer.PLAYER_JOINED_NOTIFICATION:
                    this.onNewPlayer(args[1]);
                    break;
                case HostServer.PLAYER_EXITED_NOTIFICATION:
                    this.gi.removeScoreBoardPlayer(args[1]);
                    break;
            }

        }
    }
}
