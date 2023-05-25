package ap.ex2.BookScrabbleServer;

import java.util.Scanner;

import static ap.ex2.BookScrabbleServer.MainTrain.runClient;

public class MainScrabbleServer {
    public static void main(String[] args) {
        int port = 6000;

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
