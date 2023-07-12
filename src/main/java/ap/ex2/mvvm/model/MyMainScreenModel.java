package ap.ex2.mvvm.model;

import ap.ex3.GameScrabbleServer.Saves.GameSave;
import ap.ex2.mvvm.Config;
import ap.ex2.mvvm.common.Command;
import ap.ex2.mvvm.common.Command2VM;
import ap.ex2.mvvm.model.guest.GuestGameModel;
import ap.ex2.mvvm.model.host.HostGameModel;
import ap.ex3.GameScrabbleServer.db.DBServerException;
import ap.ex3.GameScrabbleServer.rest.GameServer400;
import ap.ex3.GameScrabbleServer.rest.HttpClientManager;

import java.io.IOException;
import java.net.URISyntaxException;

public class MyMainScreenModel extends MainScreenModel {

    // if GameSave if null, ignore it
    public void startHostGameModel(String nickname, String savedGameID) {
        GameSave gameSave = null;
        boolean canStartGame;

        if (savedGameID != null) {
            canStartGame = false;

            try {
                HttpClientManager hcm = new HttpClientManager(Config.getInstance().get(Config.HTTP_BASE_URL));
                int intID = Integer.parseInt(savedGameID);
                gameSave = hcm.httpGet(intID, nickname);
                canStartGame = true;
            } catch (NumberFormatException e) {
                notifyViewModel(new String[]{"ERR", "Game ID isn't an integer."});
            } catch (DBServerException e) {
                notifyViewModel(new String[]{"ERR", "HTTP Internal server error: " + e.getMessage()});
            } catch (URISyntaxException e) {
                notifyViewModel(new String[]{"ERR", "HTTP base URL is invalid."});
            } catch (Exception e) {
                notifyViewModel(new String[]{"ERR", "HTTP encountered an exception: " + e.getMessage()});
            }

        } else {
            //try to start a new game
            canStartGame = true;
        }

        if (canStartGame) {
            this.gameModel = new HostGameModel(nickname,
                    Integer.parseInt(Config.getInstance().get(Config.HOST_PORT_KEY)),
                    Config.getInstance().get(Config.BOOK_SCRABBLE_IP_KEY),
                    Integer.parseInt(Config.getInstance().get(Config.BOOK_SCRABBLE_PORT_KEY)),
                    gameSave
            );
            this.gameModel.establishConnectionWithCallbackWrapper(this::startingModelCallback);
        }

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
