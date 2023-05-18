package ap.ex2.bookscrabble.model;

import ap.ex2.BookScrabbleServer.BookScrabbleHandler;
import ap.ex2.BookScrabbleServer.ClientHandler;
import ap.ex2.BookScrabbleServer.GenericServer;
import ap.ex2.scrabble.Board;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;
import java.util.Observable;

public class HostGameModel extends GameModel {
    private BookScrabbleClient myBookScrabbleClient; //Client for

    private GenericServer hostServer;
    private List<Socket> guestSockets;

    public HostGameModel(int hostPort, String bookScrabbleSeverIP, int bookScrabbleServerPort) {
        this.myBookScrabbleClient = new BookScrabbleClient(bookScrabbleSeverIP, bookScrabbleServerPort);
        this.hostServer = new GenericServer(hostPort, new HostGameHandler(), 4);

    }

    public void startHostServer() {
    }


    @Override
    public void update(Observable o, Object arg) {
        // todo
    }

    static class HostGameHandler implements ClientHandler {
    // todo in seperate file
        @Override
        public void handleClient(InputStream inFromclient, OutputStream outToClient) {

        }

        @Override
        public void close() {

        }
    }


}
