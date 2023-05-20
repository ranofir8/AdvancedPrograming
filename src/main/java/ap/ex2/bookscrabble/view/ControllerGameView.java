package ap.ex2.bookscrabble.view;

import ap.ex2.scrabble.Board;
import ap.ex2.scrabble.Tile;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Label;
import javafx.scene.paint.Color;

import java.net.URL;
import java.util.Observable;
import java.util.ResourceBundle;

public class ControllerGameView extends GameView implements Initializable {
    @FXML
    private Label portNum;

    @FXML
    private Label nickname;

    public ControllerGameView() {
        System.out.println("game controller created");
    }

    public void initSceneBind() {
        if (this.myViewModel == null)
            return;
        this.portNum.textProperty().bind(this.myViewModel.resultHostPort);
        this.nickname.textProperty().bind(this.myViewModel.nickname);
    }


    /**
     * @author Ran Ofir
     * join game button function in opening window -
     * opens a game if a valid connection to the inserted ip and port is foun
     */


//    /**
//     * @param sceneFXML the relevant scene (FXML window) to change to
//     */
//    private void switchToScene(String sceneFXML) {
//        try {
//            Parent root = FXMLLoader.load(R.getResource(sceneFXML));
//            Scene newScene = new Scene(root);
//            this.stage.setScene(newScene);
//            this.stage.show();
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }
//
//
//    }

    /**
     * host game button function in opening window -
     * navigate to a new game with created ip and port
     */

    public String getPortText() {
        return "Port: "+ this.portNum;
    }

    @FXML
    private Canvas boardCanvas;
    public void drawBoardTest() {
        GraphicsContext gc = this.boardCanvas.getGraphicsContext2D();
//        Board b = this.myPlayer.getGameBoard();  todo get from model
        Board b = new Board();
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



    @Override
    public void update(Observable o, Object arg) {

    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        this.initSceneBind();
    }
}