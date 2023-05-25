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
    public boolean challengeWord(String word) throws IOException {
        return this.runClient("c," + word).equals("true");

    }

    public boolean pingServer() throws IOException {
        return this.runClient("p,*").equals("pong");
    }


    public static void main(String[] args) throws IOException {
        int port = 6000;
        BookScrabbleClient bsc = new BookScrabbleClient("127.0.0.1", port);

//        boolean res = bsc.runClient("Q,magic254573");
        boolean res = bsc.queryWord("magic");
        System.out.println("server returned [q]: " + res);

        boolean res2 = bsc.challengeWord("magic");
        System.out.println("server returned [c]: " + res2);

        boolean res3 = bsc.pingServer();
        System.out.println("server returned [p]: " + res3);


//      *     ******   *****   ***   *****
//      *     *    *       *    *    *   *
//           ***   *       *    *    *   *

    }
}
