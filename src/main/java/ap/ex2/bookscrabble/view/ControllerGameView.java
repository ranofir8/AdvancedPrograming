package ap.ex2.bookscrabble.view;

import ap.ex2.bookscrabble.R;
import ap.ex2.bookscrabble.common.Command;
import ap.ex2.bookscrabble.common.guiMessage;
import ap.ex2.bookscrabble.viewModel.GameViewModel;
import ap.ex2.bookscrabble.viewModel.ViewModel;
import ap.ex2.scrabble.Board;
import ap.ex2.scrabble.Tile;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.Observable;
import java.util.ResourceBundle;

public class ControllerGameView implements View, Initializable {
    public TextField joinGameIP; //for guest
    public TextField joinGamePort; //for guest

    private GameViewModel gameViewModel;
    public BooleanProperty isHost;

    Board b;

    @FXML
    private Stage stage;
    @FXML
    private Label portNum;

    private final static String SCENE_HELLO_FXML = "hello-view.fxml";
    private final static String SCENE_GAME_FXML = "game-view.fxml";

    public ControllerGameView() {
        System.out.println("controller createed");
        this.isHost = new SimpleBooleanProperty();
        b = new Board();

    }

    public void setGameViewModel(GameViewModel gvm) {
        this.gameViewModel = gvm;
        this.initHelloSceneBind();
    }
    public void initHelloSceneBind() {
        if (this.gameViewModel == null)
            return;
        this.gameViewModel.hostPort.bind(this.joinGamePort.textProperty());
        this.gameViewModel.hostIP.bind(this.joinGameIP.textProperty());
        this.gameViewModel.isHost.bind(this.isHost);
    }

    public void initGameSceneBind() {
        this.portNum.textProperty().bind(this.gameViewModel.resultHostPort);
    }

    /**
     * @author Ran Ofir
     * join game button function in opening window -
     * opens a game if a valid connection to the inserted ip and port is foun
     */


    public void setStage(Stage stage) {
        this.stage = stage;
    }

    /**
     * @param sceneFXML the relevant scene (FXML window) to change to
     */
    private void switchToScene(String sceneFXML) {
        try {
            Parent root = FXMLLoader.load(R.getResource(sceneFXML));
            Scene newScene = new Scene(root);
            this.stage.setScene(newScene);
            this.stage.show();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }


    }

    /**
     * host game button function in opening window -
     * navigate to a new game with created ip and port
     */
    @FXML
    protected void hostNewGameClick() {
        this.isHost.set(true);
        this.gameViewModel.startGameModel();

//        try {
//            this.switchToScene(SCENE_GAME_FXML);
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }
    }

    @FXML
    protected void joinExistingGameClick() {
        this.isHost.set(false); //transmitted to vm and m(?)
        this.gameViewModel.startGameModel();

//        try {
//            this.switchToScene(SCENE_GAME_FXML);
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }
        //setChanged();
        //notifyObservers(false);
        //System.out.println("join");
    }

    public String getPortText() {
        return "Port: "+ this.portNum;
    }

    @FXML
    private Canvas boardCanvas;
    public void drawBoardTest() {
        GraphicsContext gc = this.boardCanvas.getGraphicsContext2D();
//        Board b = this.myPlayer.getGameBoard();  todo get from model

        int w = (int) this.boardCanvas.getWidth(), h = (int) this.boardCanvas.getHeight();
        int square = (int)(Math.min(w, h) / (float)Math.max(Board.ROW_NUM, Board.COL_NUM));
        //fill board with words for test:
        test_ControllerGameView t_board = new test_ControllerGameView();
        t_board.testBoard(b);
        for (int row = 0; row < Board.ROW_NUM; row++) {
            for (int col = 0; col < Board.COL_NUM; col++) {
                int m = b.getMultiplierAtInt(row, col);
                Color toFill;
                switch (m) {
                    case 11:
                        toFill = Color.color(0, 153.0/255, 0);
                        break;
                    case 13:
                        toFill = Color.color(0, 153.0/255, 1);
                        break;
                    case 12:
                        toFill = Color.color(100.0/255, 204.0/255, 1);
                        break;
                    case 21:
                        toFill = Color.color(1, 1, 153.0/255);
                        break;
                    case 31:
                        toFill = Color.color(1, 51.0/255, 51.0/255);
                        break;
                    default:
                        toFill = Color.GRAY;
                        break;
                }
                gc.setFill(toFill);
                gc.fillRect(row * square, col * square, square, square);
                gc.strokeRect(row * square, col * square, square, square);
                Tile t = b.getTileAt(row,col);
                if(t != null) {
                    gc.fillText(""+t.letter,col*square+10,row*square-10);
                }
            }
        }

    }

    private void displayMSG(guiMessage messageToDisplay) {
        Alert alert = new Alert(messageToDisplay.alert);
        alert.setTitle("Message");
        //alert.setHeaderText("message");
        alert.setContentText(messageToDisplay.message);
        alert.showAndWait();
    }

    @Override
    public void update(Observable o, Object arg) {
        if (o == this.gameViewModel) {
            if (arg instanceof guiMessage) {
                this.displayMSG((guiMessage) arg);
            } else if (arg instanceof Command) {
                this.switchToScene(SCENE_GAME_FXML);
                this.initGameSceneBind();
            }
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        if (url.equals(R.getResource(SCENE_HELLO_FXML))) {
            this.initHelloSceneBind();
        } else if (url.equals(R.getResource(SCENE_GAME_FXML))) {
            this.initGameSceneBind();
        }
    }
}