package ap.ex2.bookscrabble.model;

import ap.ex2.BookScrabbleServer.BookScrabbleHandler;
import ap.ex2.BookScrabbleServer.ClientHandler;
import ap.ex2.BookScrabbleServer.GenericServer;
import ap.ex2.scrabble.Board;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;
import java.util.Observable;

public class HostGameModel extends GameModel {
    private BookScrabbleClient myBookScrabbleClient; //for Client
    private int hostPort;
    private Socket hostSocket;
    private List<Socket> guestSockets;

    public HostGameModel(int hostPort, String bookScrabbleSeverIP, int bookScrabbleServerPort) { //String configFileName
        this.hostPort = hostPort;
        this.myBookScrabbleClient = new BookScrabbleClient(bookScrabbleSeverIP, bookScrabbleServerPort);
    }


    @Override
    public int getDisplayPort() {
        return this.hostPort;
    }

    @Override
    public void establishConnection() throws IOException {
        // bind to host port
        ServerSocket server = new ServerSocket(this.hostPort);
        server.setSoTimeout(1000); //1s
    }

    protected void finalize() {
        if (this.hostSocket != null && !this.hostSocket.isClosed()) {
            try {
                this.hostSocket.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
