package ap.ex2.bookscrabble.model.host;

import ap.ex2.BookScrabbleServer.BookScrabbleClient;
import ap.ex2.bookscrabble.common.Command;
import ap.ex2.bookscrabble.common.Command2VM;
import ap.ex2.bookscrabble.model.GameModel;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;

import java.io.IOException;
import java.util.HashSet;
import java.util.Observable;
import java.util.Observer;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;

public class HostGameModel extends GameModel implements Observer {
    private BookScrabbleClient myBookScrabbleClient; //for Client
    private int hostPort;
    private Set<String> onlinePlayers;

    private HostServer hostServer;

    public HostGameModel(String nickname, int hostPort, String bookScrabbleSeverIP, int bookScrabbleServerPort) {
        super(nickname); //String configFileName
        this.hostPort = hostPort;
        this.onlinePlayers = new HashSet<>();

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

        this.hostServer.setOnMsgReceivedCallback(
                new BiConsumer<String, String>() {
                    @Override
                    public void accept(String s, String s2) {
                        onRecvMessage(s, s2);
                    }
                }
        );

        this.hostServer.setMyNickname(this.gi.getNickname());

        //new BiFunction<>()
        this.hostServer.start();

    }

    @Override
    protected void closeConnection() {
        this.hostServer.close();
    }

    public void onRecvMessage(String nickname, String msgRecv) {
        if (nickname == null) {
            // a new player has joined
            if (this.hostServer.hasPlayerNamed(msgRecv)) {
                // welcome him to the game

            } else {
                // kick this player

            }

        }
        System.out.println("The player " + nickname+" sent: "+msgRecv);
    }

    public void sendMessageToGuest(String nickname, String msgToSend) {

    }

    protected void finalize() {
        this.hostServer.close();
    }


    @Override
    public void update(Observable o, Object arg) {
        if (o == this.hostServer) {
            String[] args = (String[]) arg;
            switch (args[0]) {
                case HostServer.SOCKET_MSG_NOTIFICATION:
                    this.onRecvMessage(args[1], args[2]);
                    break;
                case HostServer.PLAYER_JOINED_NOTIFICATION:
                    this.gi.updateScoreBoard(args[1], 0);
                    break;
                case HostServer.PLAYER_EXITED_NOTIFICATION:
                    this.gi.removeScoreBoardPlayer(args[1]);
                    break;
            }

        }
    }
}
