package ap.ex2.bookscrabble.viewModel;

import ap.ex2.bookscrabble.common.*;
import ap.ex2.bookscrabble.model.GameInstance;
import ap.ex2.bookscrabble.model.GameModel;
import ap.ex2.bookscrabble.model.MainScreenModel;
import ap.ex2.bookscrabble.view.PlayerRowView;
import ap.ex2.scrabble.Board;
import javafx.application.Platform;
import javafx.beans.property.*;
import javafx.collections.ObservableList;
import javafx.scene.control.Alert;

import java.util.Observable;

public class MyViewModel extends ViewModel {
    public ObjectProperty<Board> gameBoardProperty;
    private MainScreenModel myModel;


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
        this.gameBoardProperty = new SimpleObjectProperty<>();
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

    private void updateplayerListGUI() {
        Platform.runLater(() -> {
            this.playerScoreboard.get().setAll(myModel.getGameModel().getPlayerList());
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
        this.myModel.getGameModel().onStartGame();

//        this.hasBoardUpdated  update board
    }

    @Override
    protected void initGameModelBinds() {
        GameModel gm = this.myModel.getGameModel();
        gm.getGameInstance().scoreBoardChangeEvent
                .addListener((observableValue, oldVal, newVal) -> {
                    this.updateplayerListGUI();
                });
        this.gameBoardProperty.bind(gm.getGameInstance().gameBoardProperty);
        this.gameBoardProperty.addListener((observableValue, board, t1) -> System.out.println("gameBoard in VM updated"));

        if (gm != null)
            gm.addObserver(this);
    }
}
