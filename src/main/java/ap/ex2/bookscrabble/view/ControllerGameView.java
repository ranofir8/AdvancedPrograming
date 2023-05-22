package ap.ex2.bookscrabble.view;

import ap.ex2.scrabble.Board;
import ap.ex2.scrabble.Tile;
import javafx.beans.property.ObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.TableView;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

import java.net.URL;
import java.util.Comparator;
import java.util.Observable;
import java.util.ResourceBundle;

public class ControllerGameView extends GameView implements Initializable {
    @FXML
    private Label portNum;

    @FXML
    private Label countPlayers;

    @FXML
    private Label nickname;

    @FXML
    private TableView<PlayerRowView> scoreBoard;


    public void initSceneBind() {
        if (this.myViewModel == null)
            return;
        System.out.println("init bind vm to v");
        this.portNum.textProperty().bind(this.myViewModel.resultHostPort);
        this.nickname.textProperty().bind(this.myViewModel.nickname);
        this.countPlayers.textProperty().bind(this.myViewModel.countPlayers);
        this.myViewModel.playerScoreboard.bind(this.scoreBoard.itemsProperty());
    }







    /**
     * host game button function in opening window -
     * navigate to a new game with created ip and port
     */

    public String getPortText() {
        return "Port: "+ this.portNum;
    }

    @FXML
    public Canvas boardCanvas; // public -> private
    public void drawBoardTest() {
        GraphicsContext gc = this.boardCanvas.getGraphicsContext2D();
//        Board b = this.myPlayer.getGameBoard();  todo get from model
        Board b = new Board();
        int w = (int) this.boardCanvas.getWidth(), h = (int) this.boardCanvas.getHeight();
        int square = (int)(Math.min(w, h) / (float)Math.max(Board.ROW_NUM, Board.COL_NUM));
        //fill board with words for test:
        test_ControllerGameView t_board = new test_ControllerGameView();
        t_board.testBoard(b);
        Tile t;
        gc.setFont(Font.font("Arial", square * 0.6)); // Adjust the font size as needed
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
                gc.fillRect(col * square, row * square, square, square);
                gc.strokeRect(col * square, row * square, square, square);
                t = b.getTileAt(row,col);
                if(t != null) {
                    gc.setFill(Color.BLACK); // Set the text color to black
                    gc.fillText("" + t.letter, col * square + 10, row * square + square - 10);
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


        TableColumn<PlayerRowView, String> nicknameCol = new TableColumn<PlayerRowView,String>("Nickname");
        nicknameCol.setCellValueFactory(new PropertyValueFactory("Nickname"));
        TableColumn<PlayerRowView, String> scoreCol = new TableColumn<PlayerRowView,String>("Score");
        scoreCol.setCellValueFactory(new PropertyValueFactory("Score"));

        nicknameCol.editableProperty().set(false);
        nicknameCol.setSortable(false);
        scoreCol.editableProperty().set(false);


        this.scoreBoard.getColumns().add(nicknameCol);
        this.scoreBoard.getColumns().add(scoreCol);

        this.scoreBoard.getSortOrder().add(scoreCol);
//        this.scoreBoard.property


//        PlayerRowView player = new PlayerRowView("Gil?");
//        player.setScore(100);
//        this.scoreBoard.getItems().add(player);

        //this.scoreBoard.itemsProperty().bind();

    }
}