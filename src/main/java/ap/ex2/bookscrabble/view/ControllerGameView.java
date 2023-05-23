package ap.ex2.bookscrabble.view;

import ap.ex2.bookscrabble.common.Command;
import ap.ex2.bookscrabble.common.UpdatingObjectProperty;
import ap.ex2.scrabble.Board;
import ap.ex2.scrabble.Tile;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.TableView;
import javafx.scene.input.MouseEvent;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

import java.io.File;
import java.net.URL;
import java.util.*;

public class ControllerGameView extends GameView implements Initializable {
    private BooleanProperty isHostGame;
    private ObjectProperty<Board> gameBoardProperty;

    @FXML
    private Label portNum;

    @FXML
    private Label gameStatusLabel;

    @FXML
    private TableView<PlayerRowView> scoreBoard;

    @FXML
    private ScrollPane tilesSP;

    @FXML
    public Button startGameButton;

    public ControllerGameView() {
        this.isHostGame = new SimpleBooleanProperty();
        this.gameBoardProperty = new SimpleObjectProperty<>();

        this.tiles = new ArrayList<>();
        this.test_AddTiles();
        //this.isHost = ControllerHelloView.isHost;
    }

    public void initSceneBind() {
        if (this.myViewModel == null)
            return;
        this.isHostGame.bind(this.myViewModel.isHost);

        this.startGameButton.visibleProperty().bind(this.isHostGame); //start game button is available only to the host
        //at start there are no client so disable start game button:
        this.startGameButton.setDisable(true);

        this.portNum.textProperty().bind(this.myViewModel.resultHostPort);
        this.myViewModel.gameStatusUpdateEvent.addListener((observableValue, s, t1) -> this.gameStatusLabel.textProperty().set(this.myViewModel.getStatusText()));
        this.myViewModel.playerScoreboard.bind(this.scoreBoard.itemsProperty());
        this.gameBoardProperty.bind(this.myViewModel.gameBoardProperty);
        this.myViewModel.gameBoardProperty.addListener((observableValue, board, t1) -> System.out.println("gameBoard in V updated"));
    }

    /**
     * host game button function in opening window -
     * navigate to a new game with created ip and port
     */
    public String getPortText() {
        return "Port: "+ this.portNum;
    }


    @Override
    public void update(Observable o, Object arg) { //updates from myViewModel
        if (o == this.myViewModel) {
            if (arg instanceof Command) {
                Command cmd = (Command) arg;
                switch (cmd) {
                    case UPDATE_GAME_BOARD:
                        this.drawBoardTest();
                        break;
                }
            }
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        this.initSceneBind();

//        this.tilesSP.minViewportWidthProperty().bind(this.TilesCanvas.widthProperty());
//        this.tilesSP.prefViewportWidthProperty().bind(this.TilesCanvas.widthProperty());

        this.tilesSP.prefViewportHeightProperty().bind(this.TilesCanvas.heightProperty());

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


    }

    @FXML
    public void startGameButtonAction() {
        // unbind visibility
        this.startGameButton.visibleProperty().unbind();
        this.startGameButton.visibleProperty().set(false); //bye button

        this.myViewModel.startGame();
    }

    @FXML
    public Canvas boardCanvas; // public -> private
    private final double letterMargin = 0.25;
    private final double tilePadding = 0.1;
    public void drawBoardTest() {
        GraphicsContext gc = this.boardCanvas.getGraphicsContext2D();
//        Board b = this.myPlayer.getGameBoard();  todo get from model
        //        test_ControllerGameView t_board = new test_ControllerGameView();
//        t_board.testBoard(b);

        Board b = this.gameBoardProperty.get();


        int w = (int) this.boardCanvas.getWidth(), h = (int) this.boardCanvas.getHeight();
        int square = (int)(Math.min(w, h) / (float)Math.max(Board.ROW_NUM, Board.COL_NUM));
        //fill board with words for test:

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
                    gc.fillText("" + t.letter, (col + letterMargin) * square, (row +1-letterMargin) * square);
                }
            }
        }
        drawTiles();

    }

    @FXML
    void Clicked_on_board(MouseEvent event){
        double x = event.getX();
        double y = event.getY();
        int row=0,col =0;
        Board b = new Board();
        int w = (int) this.boardCanvas.getWidth(), h = (int) this.boardCanvas.getHeight();
        int square = (int)(Math.min(w, h) / (float)Math.max(Board.ROW_NUM, Board.COL_NUM));
        System.out.println("x = " + x + " y = " + y);
        row = (int)(x/square);
        col = (int)(y/square);
        mark_board_square(row,col,square, this.boardCanvas.getGraphicsContext2D());

        SoundManager.singleton.playSound(SoundManager.SOUND_TILE_PRESSED);
    }


    void mark_board_square(int x, int y, int square, GraphicsContext gc ){
        gc.setStroke(Color.PERU);
        gc.strokeRect(x*square, y*square, square, square);

    }

    @FXML
    public Canvas TilesCanvas; // public -> private

    private List<Tile> tiles;


    public void test_AddTiles() {
        for(int i=0; i<16 ;i++) {
            tiles.add(i, new Tile((char) ('A' + (i%26)),i*2));
        }
    }

    public void test_RemoveTile() {
        this.tiles.remove(0);
    }
    public void drawTiles() {
        this.test_RemoveTile();

        int square = (int) this.TilesCanvas.getHeight();
        this.TilesCanvas.heightProperty().set(square+0.1);
        GraphicsContext gc = this.TilesCanvas.getGraphicsContext2D();

        this.TilesCanvas.setWidth(square * (1 + this.tilePadding) * tiles.size() - tilePadding*square*0.5); // adapt canvas width

        int w = (int) this.TilesCanvas.getWidth(), h = (int) this.TilesCanvas.getHeight();


//        for (int row = 0; row < Board.ROW_NUM; row++) {
        int i = 0;

        for (Tile t : tiles) {
            double startX = i * (square * (1+tilePadding));
            gc.setFill(Color.GRAY);
            gc.fillRect(startX, 0, square, square);
            gc.strokeRect(startX, 0, square, square);
            gc.setFill(Color.BLACK); // Set the text color to black

            gc.setFont(Font.font("Arial", square * 0.6)); // Adjust the font size as needed
            gc.fillText("" + t.letter, startX + letterMargin * square, (1-letterMargin) * square);

            gc.setFont(Font.font("Arial", square * 0.2)); // Adjust the font size as needed
            double startXletter = startX + (1-letterMargin*0.6) * square;
            if (t.score > 9)
                startXletter -= letterMargin * 0.5  * square;
            gc.fillText("" + t.score, startXletter, (1-letterMargin*0.5) * square);

            i++;
        }

    }

}