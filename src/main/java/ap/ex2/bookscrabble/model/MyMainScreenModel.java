package ap.ex2.bookscrabble.model;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Observable;

public class MyMainScreenModel extends MainScreenModel {
    private HashMap<String, String> configs;

    public static final String BOOK_SCRABBLE_IP_KEY = "book_scrabble_ip";
    public static final String BOOK_SCRABBLE_PORT_KEY = "book_scrabble_port";

    public MyMainScreenModel(String configFileName) {
        this.configs = new HashMap<String, String>();

        try {
            BufferedReader bf = new BufferedReader(new FileReader(configFileName));

            bf.lines().map(l -> l.split(",")).filter(stArr -> stArr.length == 2).forEach(arr -> configs.put(arr[0], arr[1]));

            bf.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void startGameModel(boolean isHost) {
        if (isHost) {
            int hostPort = getAvailableHostPort();
            this.gameModel = new HostGameModel(hostPort, this.configs.get(BOOK_SCRABBLE_IP_KEY),Integer.parseInt(this.configs.get(BOOK_SCRABBLE_PORT_KEY)));

        } else {
            this.gameModel = new GuestGameModel();
        }
        try {
            this.gameModel.establishConnection();
        } catch (Exception e) {
            // display to GUI "unable to establish connection, try again"
            notifyObservers(new String[]{"MSG", "Unable to establish connection"});
        }
    }

    private int getAvailableHostPort() {
        int gilad = 100;
        return gilad;
    }
}
