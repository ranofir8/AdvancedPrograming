package ap.ex2.bookscrabble.model;

import ap.ex2.bookscrabble.common.Command;
import ap.ex2.bookscrabble.common.Command2VM;

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
    public static final String HOST_PORT_KEY = "host_port";

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
    public void startHostGameModel() {
        this.gameModel = new HostGameModel(Integer.parseInt(this.configs.get(HOST_PORT_KEY)), this.configs.get(BOOK_SCRABBLE_IP_KEY), Integer.parseInt(this.configs.get(BOOK_SCRABBLE_PORT_KEY)));
        this.afterStartingModel();
    }

    private void afterStartingModel() {
        try {
            this.gameModel.establishConnection();
        } catch (Exception e) {
            // display to GUI "unable to establish connection, try again"
            setChanged();
            notifyObservers(new String[]{"MSG", "Unable to establish connection"});
        }

        setChanged();
        notifyObservers(new Command2VM(Command.GO_TO_GAME_SCENE));
        setChanged();
        notifyObservers(new Command2VM(Command.DISPLAY_PORT, this.gameModel.getDisplayPort()));
    }

    @Override
    public void startGuestGameModel(String hostIPinput, int hostPortInput) {
        this.gameModel = new GuestGameModel(hostIPinput, hostPortInput);
        this.afterStartingModel();
    }

}
