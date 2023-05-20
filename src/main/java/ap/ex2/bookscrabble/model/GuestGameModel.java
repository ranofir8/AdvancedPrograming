package ap.ex2.bookscrabble.model;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Observable;

public class GuestGameModel extends GameModel {
    private String hostIP;
    private int hostPort;

    private Socket hostSocket;


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
        this.hostSocket = new Socket(this.hostIP, this.hostPort);



        // temp code here - check connection
        PrintWriter pw = new PrintWriter(this.hostSocket.getOutputStream());
        pw.println(this.gi.getNickname());
        pw.flush();

        // close socket! todo
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
