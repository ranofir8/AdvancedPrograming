package ap.ex2.bookscrabble.model;

import ap.ex2.BookScrabbleServer.BookScrabbleHandler;
import ap.ex2.BookScrabbleServer.GenericServer;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;
import java.util.Observable;

public class HostGameModel extends GameModel {
    private BookScrabbleClient myBookScrabbleClient;

    private GenericServer hostServer;
    private List<Socket> guestSockets;

    public HostGameModel(int hostPort, String bookScrabbleSeverIP, int bookScrabbleServerPort) {
        this.myBookScrabbleClient = new BookScrabbleClient(bookScrabbleSeverIP, bookScrabbleServerPort);
        this.hostServer = new GenericServer(hostPort, null, 4);

    }

    public void startHostServer() {

    }


    @Override
    public void update(Observable o, Object arg) {

    }
}
