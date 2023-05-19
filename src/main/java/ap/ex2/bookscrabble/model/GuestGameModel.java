package ap.ex2.bookscrabble.model;

import java.io.IOException;
import java.net.Socket;
import java.util.Observable;

public class GuestGameModel extends GameModel {
    private String hostIP;
    private int hostPort;

    private Socket hostSocket;

    public GuestGameModel(String hostIP, int hostPort) {
        this.hostIP = hostIP;
        this.hostPort = hostPort;
    }

    @Override
    public void establishConnection() throws Exception {
        this.hostSocket = new Socket(this.hostIP, this.hostPort);
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
