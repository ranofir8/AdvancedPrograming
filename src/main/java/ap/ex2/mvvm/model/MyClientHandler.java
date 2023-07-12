package ap.ex2.mvvm.model;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.SocketException;
import java.util.Observable;

public class MyClientHandler extends Observable implements SocketClientHandler {
    private final Socket mySocket;
    private BufferedReader inFromClient;
    private PrintWriter outToClient;
    private volatile boolean isStop;

    public static final int CLIENT_CONNECTION_CLOSED = 10;

    public MyClientHandler(Socket clientSocket) {
        this.mySocket = clientSocket;
        this.isStop = true;
    }

    @Override
    public void startHandlingClient() {
        this.isStop = false;
        try {
            this.inFromClient = new BufferedReader(new InputStreamReader(this.mySocket.getInputStream()));
            this.outToClient = new PrintWriter(this.mySocket.getOutputStream());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        Runnable r = () -> {
            while (!isStop) {
                try {
                    String msgFromClient = inFromClient.readLine();
                    setChanged();
                    notifyObservers(msgFromClient);

                } catch (SocketException e) {
                    setChanged();
                    notifyObservers(CLIENT_CONNECTION_CLOSED);
                    System.out.println("client has left! closing this connection...");
                    close();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }

        };

        new Thread(r).start();
    }

    public void sendMsg(String msgToSend) {
        this.outToClient.println(msgToSend);
        this.outToClient.flush();
    }

    @Override
    public void close() {
        System.out.println("Closed client handler");
        this.isStop = true;
        this.outToClient.close();
        try {
            this.inFromClient.close();
            this.mySocket.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }
}
