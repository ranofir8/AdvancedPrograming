package ap.ex2.BookScrabbleServer;


import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;

public class BookScrabbleHandler implements ClientHandler {
    private PrintWriter out;
    private BufferedReader in;

    @Override
    public void handleClient(InputStream inFromClient, OutputStream outToClient) {
        this.in = new BufferedReader(new InputStreamReader(inFromClient));
        this.out = new PrintWriter(outToClient);

        String inputString = null;
        try {
            inputString = this.in.readLine();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        String[] splittedArray = inputString.split(",");
        ArrayList<String> splittedList = new ArrayList<>(Arrays.asList(splittedArray));

        char clientCharacter = splittedList.remove(0).toLowerCase().charAt(0);
        String answer = "";
        DictionaryManager dm = DictionaryManager.get();
        String[] parameters = splittedList.toArray(new String[]{}); // (String[]) splittedList.toArray();
        switch (clientCharacter) {
            case 'q':
                answer = ""+dm.query(parameters);
                break;
            case 'c':
                answer = ""+dm.challenge(parameters);
                break;
            default:
                answer = "invalid query";
                break;
        }
        this.out.println(answer);
        this.out.flush();

    }

    @Override
    public void close() {
        try {
            this.out.close();
            this.in.close();
        } catch (IOException ignored) {}

    }
}
