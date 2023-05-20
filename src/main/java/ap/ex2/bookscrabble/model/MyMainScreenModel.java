package ap.ex2.bookscrabble.model;

import ap.ex2.bookscrabble.common.Command;
import ap.ex2.bookscrabble.common.Command2VM;
import ap.ex2.bookscrabble.model.guest.GuestGameModel;
import ap.ex2.bookscrabble.model.host.HostGameModel;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;

public class MyMainScreenModel extends MainScreenModel {
    private HashMap<String, String> configs;

    public static final String BOOK_SCRABBLE_IP_KEY = "book_scrabble_ip";
    public static final String BOOK_SCRABBLE_PORT_KEY = "book_scrabble_port";
    public static final String HOST_PORT_KEY = "host_port";
    public static final String DEFAULT_GUEST_IP_KEY = "default_guest_ip";
    public static final String DEFAULT_GUEST_PORT_KEY = "default_guest_port";

    public MyMainScreenModel(String configFileName) {
        this.configs = new HashMap<String, String>();

        try {
            BufferedReader bf = new BufferedReader(new FileReader(configFileName));

            bf.lines().map(l -> l.split(",")).filter(stArr -> stArr.length == 2).forEach(arr -> configs.put(arr[0], arr[1]));

            bf.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        this.updateDefaultValues();
    }

    private void updateDefaultValues() {
        setChanged();
        notifyObservers(new Command2VM(Command.DEFAULT_GUEST_VALUES, new String[]{this.configs.get(DEFAULT_GUEST_IP_KEY), this.configs.get(DEFAULT_GUEST_PORT_KEY)}));
    }

    @Override
    public void startHostGameModel(String nickname) {
        this.gameModel = new HostGameModel(nickname, Integer.parseInt(this.configs.get(HOST_PORT_KEY)), this.configs.get(BOOK_SCRABBLE_IP_KEY), Integer.parseInt(this.configs.get(BOOK_SCRABBLE_PORT_KEY)));
        this.afterStartingModel();
    }

    private void afterStartingModel() {
        try {
            this.gameModel.establishConnection();

            if (this.gameModel instanceof HostGameModel) {
                setChanged();
                notifyObservers(new Command2VM(Command.GO_TO_GAME_SCENE));
                setChanged();
                notifyObservers(new Command2VM(Command.DISPLAY_PORT, this.gameModel.getDisplayPort()));
            }

        } catch (Exception e) {
            // display to GUI "unable to establish connection, try again"
            setChanged();
            notifyObservers(new String[]{"MSG", "Unable to establish connection: " + e.getMessage()});
        }


    }

    @Override
    public void startGuestGameModel(String nickname, String hostIPinput, int hostPortInput) {
        this.gameModel = new GuestGameModel(nickname, hostIPinput, hostPortInput);
        this.afterStartingModel();
    }

}
