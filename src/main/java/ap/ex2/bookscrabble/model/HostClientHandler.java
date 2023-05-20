package ap.ex2.bookscrabble.model;

import ap.ex2.BookScrabbleServer.SimpleClientHandler;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Observable;

public class HostClientHandler extends Observable implements SocketClientHandler {
    private Socket mySocket;
    private BufferedReader inFromClient;
    private PrintWriter outToClient;
    private volatile boolean isStop;

    public HostClientHandler(Socket clientSocket) {
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
                    System.out.println("waiting for msg: " +  inFromClient);
                    String msgFromClient = inFromClient.readLine();
                    System.out.println("the message received, it's: "+ msgFromClient);
                    setChanged();
                    notifyObservers(msgFromClient);

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
