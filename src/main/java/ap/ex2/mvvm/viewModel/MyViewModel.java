package ap.ex2.mvvm.viewModel;

import ap.ex2.mvvm.common.Command;
import ap.ex2.mvvm.common.Command2VM;
import ap.ex2.mvvm.common.guiMessage;
import ap.ex2.mvvm.model.GameInstance;
import ap.ex2.mvvm.model.GameModel;
import ap.ex2.mvvm.model.MainScreenModel;
import ap.ex2.mvvm.model.host.HostGameModel;
import ap.ex2.mvvm.view.PlayerTableRow;
import ap.ex3.GameScrabbleServer.Saves.GameSave;
import javafx.application.Platform;
import javafx.beans.property.*;
import javafx.collections.ObservableList;
import javafx.scene.control.Alert;

import java.util.List;
import java.util.Observable;

public class MyViewModel extends ViewModel {
    public ObjectProperty<GameInstance> gameInstanceProperty;


    public IntegerProperty countPlayers;
    public BooleanProperty canStartGame;


    public BooleanProperty isHost; // is creating/joining game
    public StringProperty hostPort;
    public StringProperty hostIP;

    public StringProperty loadGameIDTextField;
    public StringProperty nicknamePropertyTextField;
    public StringProperty gameStatusStringProperty;

    public StringProperty resultHostPort;
    public ObjectProperty<ObservableList<PlayerTableRow>> playerScoreboard;

    public MyViewModel(MainScreenModel myModel) {
        this.myModel = myModel;

        this.isHost = new SimpleBooleanProperty();
        this.hostPort = new SimpleStringProperty();
        this.hostIP = new SimpleStringProperty();

        this.resultHostPort = new SimpleStringProperty();
        this.nicknamePropertyTextField = new SimpleStringProperty();
        this.gameStatusStringProperty = new SimpleStringProperty();
        this.loadGameIDTextField = new SimpleStringProperty();

        this.playerScoreboard = new SimpleObjectProperty<>();
        this.gameInstanceProperty = new SimpleObjectProperty<>();
        this.countPlayers = new SimpleIntegerProperty();
        this.canStartGame = new SimpleBooleanProperty();
    }



    @Override
    public void update(Observable o, Object arg) { //updates from MyMainScreen Model
        if (o == this.myModel || o == this.myModel.getGameModel()) {
            if (arg instanceof String[]) {  // short messages that are sent to the View
                String[] args = (String[]) arg;

                setChanged();
                switch (args[0]) {
                    case "MSG":
                        notifyObservers(new guiMessage(args[1], Alert.AlertType.INFORMATION));
                        break;

                    case "ERR":
                        notifyObservers(new guiMessage(args[1], Alert.AlertType.ERROR));
                        break;

                    case "SAVE":
                    case "CRASH":
                        notifyObservers(args);
                        break;
                }
            } else if (arg instanceof Command) { // Command are directed to the view layer
                setChanged();
                notifyObservers(arg);
            } else if (arg instanceof Command2VM) { //Command2VM are handled here!
                // things that are sent to the ViewModel using Command2VM
                Command2VM cmd = (Command2VM) arg;
                switch (cmd.command) {
                    case GO_TO_GAME_SCENE:
                        this.showGameScene();
                        break;

                    case DISPLAY_PORT:
                        String s = "port is: "+(int) cmd.args;
                        this.resultHostPort.set(s);
                        break;

                    case UPDATE_GAME_STATUS_TEXT:
                        Platform.runLater(() ->this.gameStatusStringProperty.set(this.myModel.getGameStatusText()));
                        break;
                }
            }
        }
    }

    private void updatePlayerListGUI() {
        Platform.runLater(() -> {
            List<PlayerTableRow> l = myModel.getGameModel().getPlayerList();
            this.playerScoreboard.get().setAll(l);
            this.countPlayers.set(l.size());
        });
    }

    // when creating a new game, ID will be null
    public void startGameModel(String savedGameID) {
        if (this.isHost.get()) {
            this.myModel.startHostGameModel(this.nicknamePropertyTextField.get(), savedGameID);
        } else {
            try {
                int hostIntPort = Integer.parseInt(this.hostPort.get());
                this.myModel.startGuestGameModel(this.nicknamePropertyTextField.get(), this.hostIP.get(), hostIntPort);

            } catch (NumberFormatException e) {
                setChanged();
                notifyObservers(new guiMessage("Host port is invalid!", Alert.AlertType.ERROR));
            }
        }

        this.initGameModelBinds();
        this.gameInstanceProperty.get().gameStatusChangeEvent.alertChanged();
    }

    @Override
    public void startGameModel() {
        this.startGameModel(null);
    }

    @Override
    protected String getTitleText() {
        return "In game - " + this.myModel.getGameModel().getGameInstance().getNickname();
    }

    @Override
    public void startGame() {
        if (isHost.get())
            ((HostGameModel)this.myModel.getGameModel()).hostStartGame();

//        this.hasBoardUpdated  update board
    }

    @Override
    protected void initGameModelBinds() {
        GameModel gm = this.myModel.getGameModel();
        gm.getGameInstance().scoreBoardChangeEvent
                .addListener((observableValue, oldVal, newVal) -> {
                    this.updatePlayerListGUI();
                });
        gm.getGameInstance().boardTilesChangeEvent.addListener((observableValue, aBoolean, t1) -> {
            setChanged();
            notifyObservers(Command.UPDATE_GAME_CANVASES);
        });
        this.gameInstanceProperty.bind(gm.gameInstanceProperty);
        if (gm instanceof HostGameModel)
            this.canStartGame.bind(((HostGameModel) gm).canTheGameStartProperty);


        if (gm != null)
            gm.addObserver(this);
    }

    @Override
    public void sendWord() {
        this.myModel.getGameModel().requestSendWord();
    }

    @Override
    public void requestChallenge() {
        this.myModel.getGameModel().requestChallenge();
    }

    @Override
    public void giveUpTurn() {
        this.myModel.getGameModel().requestGiveUpTurn();
    }


    public void saveGameClicked() {
        if (isHost.get())
            ((HostGameModel)this.myModel.getGameModel()).saveGame();
        else
            notifyObservers(new guiMessage("Only the host can save a game.", Alert.AlertType.ERROR));
    }

    @Override
    public void loadGameClicked() {
        this.startGameModel(this.loadGameIDTextField.get());
    }
}
