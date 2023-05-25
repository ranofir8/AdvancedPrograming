package ap.ex2.bookscrabble.model;

import ap.ex2.bookscrabble.Config;
import ap.ex2.bookscrabble.common.Command;
import ap.ex2.bookscrabble.common.Command2VM;
import ap.ex2.bookscrabble.model.guest.GuestGameModel;
import ap.ex2.bookscrabble.model.host.HostGameModel;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;

public class MyMainScreenModel extends MainScreenModel {
    @Override
    public void startHostGameModel(String nickname) {
        this.gameModel = new HostGameModel(nickname,
                Integer.parseInt(Config.getInstance().get(Config.HOST_PORT_KEY)),
                Config.getInstance().get(Config.BOOK_SCRABBLE_IP_KEY),
                Integer.parseInt(Config.getInstance().get(Config.BOOK_SCRABBLE_PORT_KEY)));
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

    public String getGameStatusText() {
        if (this.getGameModel() == null)
            return "No game model.";
        GameInstance gi = this.getGameModel().getGameInstance();
        String currentGameStatus = gi.getCurrentGameStatus();
        return "Your nickname: " + gi.getNickname() + " ; " + currentGameStatus; //+ " hello";
    }
}
