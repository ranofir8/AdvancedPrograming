package ap.ex2.bookscrabble.viewModel;

import ap.ex2.bookscrabble.common.*;
import ap.ex2.bookscrabble.model.GameInstance;
import ap.ex2.bookscrabble.model.GameModel;
import ap.ex2.bookscrabble.model.MainScreenModel;
import ap.ex2.bookscrabble.model.host.HostGameModel;
import ap.ex2.bookscrabble.view.PlayerRowView;
import javafx.application.Platform;
import javafx.beans.property.*;
import javafx.collections.ObservableList;
import javafx.scene.control.Alert;

import java.util.List;
import java.util.Observable;

public class MyViewModel extends ViewModel {
    public ObjectProperty<GameInstance> gameInstanceProperty;
    private MainScreenModel myModel;

    public IntegerProperty countPlayers;


    public BooleanProperty isHost; // is creating/joining game
    public StringProperty hostPort;
    public StringProperty hostIP;

    public StringProperty nicknamePropertyTextField;
    public ChangeBooleanProperty gameStatusUpdateEvent;

    public StringProperty resultHostPort;
    public ObjectProperty<ObservableList<PlayerRowView>> playerScoreboard;

    public MyViewModel(MainScreenModel myModel) {
        this.myModel = myModel;

        this.isHost = new SimpleBooleanProperty();
        this.hostPort = new SimpleStringProperty();
        this.hostIP = new SimpleStringProperty();

        this.resultHostPort = new SimpleStringProperty();
        this.nicknamePropertyTextField = new SimpleStringProperty();
        this.gameStatusUpdateEvent = new ChangeBooleanProperty();

        this.playerScoreboard = new SimpleObjectProperty<>();
        this.gameInstanceProperty = new SimpleObjectProperty<>();
        this.countPlayers = new SimpleIntegerProperty();
    }



    @Override
    public void update(Observable o, Object arg) { //updates from MyMainScreen Model
        if (o == this.myModel || o == this.myModel.getGameModel()) {
            if (arg instanceof String[]) {
                String[] args = (String[]) arg;
                if (args[0].equals("MSG")) {
                    setChanged();
                    notifyObservers(new guiMessage(args[1], Alert.AlertType.INFORMATION));
                }
            } else if (arg instanceof Command) {
                setChanged();
                notifyObservers(arg);
            } else if (arg instanceof Command2VM) {
                Command2VM cmd = (Command2VM) arg;
                switch (cmd.command) {
                    case GO_TO_GAME_SCENE:
                        this.showGameScene();
                        break;
                    case DISPLAY_PORT:
                        String s = "port is: "+(int) cmd.args;
                        this.resultHostPort.set(s);
                        break;
                }
            }
        }
    }

    private void updatePlayerListGUI() {
        Platform.runLater(() -> {
            List<PlayerRowView> l = myModel.getGameModel().getPlayerList();
            this.playerScoreboard.get().setAll(l);
            this.countPlayers.set(l.size());
        });
    }


    @Override
    public void startGameModel() {
        //nickName handling:
        //port handling:

        if (this.isHost.get()) {
            this.myModel.startHostGameModel(this.nicknamePropertyTextField.get());
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
    }

    @Override
    protected String getTitleText() {
        return "In game - " + this.myModel.getGameModel().getGameInstance().getNickname();
    }

    public String getStatusText() {
        if (this.myModel.getGameModel() == null) {
            return "No game model.";
        }
        GameInstance gi = this.myModel.getGameModel().getGameInstance();
        String currentGameStatus = gi.getCurrentGameStatus();
        return "Nickname: " + gi.getNickname() + " ; " + currentGameStatus + " hello";
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
        this.gameInstanceProperty.bind(gm.gameInstanceProperty);
        this.gameInstanceProperty.addListener((observableValue, o0, o1) -> System.out.println("gameInstance (Board) in VM updated"));

        if (gm != null)
            gm.addObserver(this);
    }

    @Override
    protected void sendWord() {
        // todo - move limboTiles list to view, send it here
//        this.myModel.getGameModel().sendWord();
    }

    @Override
    protected void requestChallenge() {

//        this.myModel.getGameModel().requestChallenge();
    }

    @Override
    protected void giveUpTurn() {
//        this.myModel.getGameModel().giveupTurn();
    }
}
