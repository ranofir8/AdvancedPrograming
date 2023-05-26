package ap.ex2.BookScrabbleServer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class BookScrabbleClient {
    private String ip;
    private int port;
    private BufferedReader inFromClient;
    private PrintWriter outToClient;
    private Socket mySocket;



    public BookScrabbleClient(String ip, int port) {
        this.ip = ip;
        this.port = port;
    }

    private String runClient(String query) throws IOException {
        Socket bookServer = new Socket(this.ip, this.port);
        PrintWriter out = new PrintWriter(bookServer.getOutputStream(), true);
        Scanner in = new Scanner(bookServer.getInputStream());
        out.println(query);

        String res = in.nextLine();

        in.close();
        out.close();
        bookServer.close();

        return res;
    }

    // add Q, to word
    public boolean queryWord(String word) throws IOException {
        return this.runClient("q," + word).equals("true");
    }

    // add C, to word
    public boolean challengeWord(String word) {
        try {
            return this.runClient("c," + word).equals("true");
        } catch (IOException e) {
            System.out.println("challenge exception :(");
            return false;
        }

    }

    public boolean pingServer() {
        try {
            return this.runClient("p,*").equals("pong");
        } catch (IOException e) {
            return false;
        }
    }
}
