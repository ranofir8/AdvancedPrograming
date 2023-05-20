package ap.ex2.bookscrabble.model.host;

import ap.ex2.BookScrabbleServer.BookScrabbleClient;
import ap.ex2.bookscrabble.model.GameModel;

import java.io.IOException;
import java.util.Observable;
import java.util.function.BiConsumer;

public class HostGameModel extends GameModel {
    private BookScrabbleClient myBookScrabbleClient; //for Client
    private int hostPort;
    private HostServer hostServer;

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



}
