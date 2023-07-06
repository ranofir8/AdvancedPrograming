package ap.ex2.bookscrabble.model;

import ap.ex3.GameScrabbleServer.Saves.test_GameSave;
import ap.ex2.bookscrabble.Config;
import ap.ex2.bookscrabble.common.Command;
import ap.ex2.bookscrabble.common.Command2VM;
import ap.ex2.bookscrabble.model.guest.GuestGameModel;
import ap.ex2.bookscrabble.model.host.HostGameModel;

public class MyMainScreenModel extends MainScreenModel {
    @Override
    public void startHostGameModel(String nickname) {
        this.gameModel = new HostGameModel(nickname,
                Integer.parseInt(Config.getInstance().get(Config.HOST_PORT_KEY)),
                Config.getInstance().get(Config.BOOK_SCRABBLE_IP_KEY),
                Integer.parseInt(Config.getInstance().get(Config.BOOK_SCRABBLE_PORT_KEY)),
                test_GameSave.createDummyObject() // todo replace with a REST request and such
                );
        this.gameModel.establishConnectionWithCallbackWrapper(this::startingModelCallback);
    }

    private void startingModelCallback(Exception e) {
        if (e == null) {
            if (this.gameModel instanceof HostGameModel) {

                setChanged();
                notifyObservers(new Command2VM(Command.GO_TO_GAME_SCENE));
                setChanged();
                notifyObservers(new Command2VM(Command.DISPLAY_PORT, this.gameModel.getDisplayPort()));
            }
        } else {
            // display to GUI "unable to establish connection, try again"
            setChanged();
            notifyObservers(new String[]{"ERR", "Unable to establish connection: " + e.getMessage()});
        }
    }

    @Override
    public void startGuestGameModel(String nickname, String hostIPinput, int hostPortInput) {
        this.gameModel = new GuestGameModel(nickname, hostIPinput, hostPortInput);
        this.gameModel.establishConnectionWithCallbackWrapper(this::startingModelCallback);
    }

    public String getGameStatusText() {
        if (this.getGameModel() == null)
            return "No game model.";
        GameInstance gi = this.getGameModel().getGameInstance();
        String currentGameStatus = this.getGameModel().getCurrentGameStatus();
        if (currentGameStatus == null)
            currentGameStatus = "Unknown game state";
        return "Your nickname: " + gi.getNickname() + " ; " + currentGameStatus; //+ " hello";
    }
}
