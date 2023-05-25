package ap.ex2.BookScrabbleServer;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class BookScrabbleClient {
    private String ip;
    private int port;

    public BookScrabbleClient(String ip, int port) {
        this.ip = ip;
        this.port = port;
    }

    public boolean runClient(String query) throws IOException {
        Socket bookServer = new Socket(this.ip, this.port);
        PrintWriter out = new PrintWriter(bookServer.getOutputStream(), true);
        Scanner in = new Scanner(bookServer.getInputStream());
        out.println(query);

        String res = in.nextLine();

        in.close();
        out.close();
        bookServer.close();

        return res.equals("true");
    }

    public static void main(String[] args) throws IOException {
        int port = 6000;

        Socket server=new Socket("localhost",port);
        PrintWriter out=new PrintWriter(server.getOutputStream());
        Scanner in=new Scanner(server.getInputStream());
        out.println("connect");

        //BookScrabbleClient bsc = new BookScrabbleClient("127.0.0.1", port);
        //runClient(port, "Q,HarryPotter.txt,Harry", true);
        /*runClient(port, "Q,s1.txt,s2.txt,"+s2[4], true);
        runClient(port, "Q,s1.txt,s2.txt,2"+s1[1], false);
        runClient(port, "Q,s1.txt,s2.txt,3"+s2[4], false);
        runClient(port, "C,s1.txt,s2.txt,"+s1[9], true);
        runClient(port, "C,s1.txt,s2.txt,#"+s2[1], false);*/

    }
}
