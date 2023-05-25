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

}
