package ap.ex2.BookScrabbleServer;

import java.io.IOException;
import java.net.ConnectException;

import static java.lang.System.exit;

public class BookClientTestProg {
    public static void main(String[] args) throws IOException {
        int port = 6000;
        BookScrabbleClient bsc = new BookScrabbleClient("127.0.0.1", port);

//        boolean res = bsc.runClient("Q,magic254573");
        String testWord = "elvin";

        System.out.println("=== Testing word: " + testWord + " ===");

        System.out.print("Ping:\t\t");
        boolean resP = bsc.pingServer();
        printHelper(resP, "Good", "Bad");

        if (!resP)
            exit(0);

        System.out.print("Query:\t\t");
        boolean resQ = bsc.queryWord(testWord);
        printHelper(resQ, "Found", "Not Found");

        System.out.print("Challenge:\t");
        boolean resC = bsc.challengeWord(testWord);
        printHelper(resC, "Found", "Not Found");
    }

    public static void printHelper(boolean t, String tr, String fl) {
        if (t)
            System.out.println("\u001B[32m" + tr + "\u001B[0m");
        else
            System.out.println("\u001B[31m" + fl + "\u001B[0m");
    }
}
