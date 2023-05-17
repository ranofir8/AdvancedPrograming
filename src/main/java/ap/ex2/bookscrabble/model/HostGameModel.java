package ap.ex2.bookscrabble.model;

import ap.ex2.BookScrabbleServer.BookScrabbleHandler;
import ap.ex2.BookScrabbleServer.GenericServer;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;

public class HostGameModel extends GameModel {
    private BookScrabbleClient myBookScrabbleClient;

    private GenericServer hostServer;
    private List<Socket> guestSockets;

    public HostGameModel(int hostPort, String bookScrabbleSeverIP, int bookScrabbleServerPort) {
        this.myBookScrabbleClient = new BookScrabbleClient(bookScrabbleSeverIP, bookScrabbleServerPort);
        this.hostServer = new GenericServer(hostPort, new HostGameHandler(), 4);
        new ServerSocket(this.port);
    }

    public void startHostServer() {
        GenericServer s =new GenericServer(port, new BookScrabbleHandler(),1);
    }



}
