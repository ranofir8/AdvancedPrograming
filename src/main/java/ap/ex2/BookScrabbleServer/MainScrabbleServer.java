package ap.ex2.BookScrabbleServer;

import ap.ex2.mvvm.Config;

import java.util.Scanner;

public class MainScrabbleServer {
    public static void main(String[] args) {
        int port = Integer.parseInt(Config.getInstance().get(Config.BOOK_SCRABBLE_PORT_KEY));

        GenericServer s = new GenericServer(port, new BookScrabbleHandler(), 1);
        System.out.println("Running BookScabbleServer on port " + port);
        s.start();
        System.out.println("Press enter to shutdown");
        Scanner sc = new Scanner(System.in);
        sc.nextLine();
        System.out.println("server closed.");
        s.close();
    }
}
