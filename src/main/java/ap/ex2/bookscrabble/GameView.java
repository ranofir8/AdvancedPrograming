package ap.ex2.bookscrabble;

import ap.ex2.scrabble.Board;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.stage.Stage;

import java.io.IOException;

public class GameView extends View {
    public TextField joinGameIP;
    public TextField joinGamePort;

    @FXML
    private Stage stage;
    private final static String SCENE_HELLO_FXML = "hello-view.fxml";
    private final static String SCENE_GAME_FXML = "game-view.fxml";


    /**
     * @author Ran Ofir
     * join game button function in opening window -
     * opens a game if a valid connection to the inserted ip and port is foun
     */
    @FXML
    protected void joinExistingGameClick() {
        try {
            String ip = joinGameIP.getText();
            String port = joinGamePort.getText();
            this.switchToScene(SCENE_GAME_FXML);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        System.out.println("join");
    }

    public void setStage(Stage stage) {
        this.stage = stage;
        System.out.println("stage is now" + stage);
    }

    /**
     * @author Gilad Savoray
     * @param sceneFXML the relevant scene (FXML window) to change to
     */
    private void switchToScene(String sceneFXML) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource(sceneFXML));

        Scene newScene = new Scene(root);
        this.stage.setScene(newScene);
        this.stage.show();
    }

    /**
     * host game button function in opening window -
     * navigate to a new game with created ip and port
     */
    @FXML
    protected void hostNewGameClick() {
        try {
            this.switchToScene(SCENE_GAME_FXML);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        System.out.println("host");
    }

    @FXML
    private Canvas boardCanvas;
    public void drawBoardTest() {
        GraphicsContext gc = this.boardCanvas.getGraphicsContext2D();
//        Board b = this.myPlayer.getGameBoard();  todo get from model
        Board b = new Board();

        int w = (int) this.boardCanvas.getWidth(), h = (int) this.boardCanvas.getHeight();
        int square = (int)(Math.min(w, h) / (float)Math.max(Board.ROW_NUM, Board.COL_NUM));

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
            }
        }

    }
}