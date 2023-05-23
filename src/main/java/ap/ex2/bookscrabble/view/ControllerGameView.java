package ap.ex2.bookscrabble.view;

import ap.ex2.bookscrabble.common.Command;
import ap.ex2.bookscrabble.common.guiMessage;
import ap.ex2.bookscrabble.model.GameModel;
import ap.ex2.scrabble.Board;
import ap.ex2.scrabble.Tile;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.*;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.TableView;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

import java.net.URL;
import java.util.*;

public class ControllerGameView extends GameView implements Initializable {
    private BooleanProperty isHostGame;

    private IntegerProperty playersCount;
    private BooleanProperty isPlayerTurn;

    // board & tiles
    private List<Tile> tilesInHand;
    private ObjectProperty<Board> gameBoardProperty;

    // tile drawing
    private final double letterMargin = 0.25;
    private final double tilePadding = 0.1;
    private double squareOfBoard = 1;
    private double squareOfTiles = 1;
    private static final Color boardSelectionBG = Color.color(1, 60.0/255, 200.0/255);

    // board selections
    private final HashMap<Integer, Tile> tilesPlaced = new HashMap<>();
    private int selectedBoardRow = -1;
    private int selectedBoardCol = -1;
    private int selectedTileIndex = -1;

    @FXML
    private Label portNum;
    @FXML
    private Label gameStatusLabel;
    @FXML
    private TableView<PlayerRowView> scoreBoard;
    @FXML
    private ScrollPane tilesSP;
    @FXML
    private Button startGameButton;
    @FXML
    private Canvas TilesCanvas;
    @FXML
    private Canvas boardCanvas;


    public ControllerGameView() {
        this.isHostGame = new SimpleBooleanProperty();
        this.gameBoardProperty = new SimpleObjectProperty<>();
        this.playersCount = new SimpleIntegerProperty();
        this.isPlayerTurn = new SimpleBooleanProperty();

        this.tilesInHand = new ArrayList<>();
        this.test_AddTiles();

        this.isPlayerTurn.set(true);  // for testing only todo: remove
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
        this.playersCount.bind(this.myViewModel.countPlayers);

        this.playersCount.addListener((observableValue, n0, n1) -> this.startGameButton.setDisable(n1.intValue()< GameModel.MIN_PLAYERS));

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
    public void update(Observable o, Object arg) {
        if (o == this.myViewModel) {
            if (arg instanceof Command) {
                Command cmd = (Command) arg;
                switch (cmd) {
                    case UPDATE_GAME_BOARD:
                        this.drawGameBoard();
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

    public void drawCanvases() {
        this.drawGameBoard();
        this.drawTiles();
    }

    public void drawGameBoard() {
        GraphicsContext gc = this.boardCanvas.getGraphicsContext2D();
        gc.clearRect(0, 0, this.boardCanvas.getWidth(), this.boardCanvas.getHeight());

        Board b = this.gameBoardProperty.get();

        int w = (int) this.boardCanvas.getWidth(), h = (int) this.boardCanvas.getHeight();
        this.squareOfBoard = (int)(Math.min(w, h) / (float)Math.max(Board.ROW_NUM, Board.COL_NUM));
        double square = this.squareOfBoard;

        gc.setFont(Font.font("Arial", square * 0.6)); // Adjust the font size as needed
        for (int row = 0; row < Board.ROW_NUM; row++) {
            for (int col = 0; col < Board.COL_NUM; col++) {
                int m = b.getMultiplierAtInt(row, col);
                Color toFill;
                if (row == this.selectedBoardRow && col == this.selectedBoardCol) {
                    toFill = ControllerGameView.boardSelectionBG;
                } else {
                    switch (m) {
                        case 11:
                            toFill = Color.color(0, 153.0 / 255, 0);
                            break;
                        case 13:
                            toFill = Color.color(0, 153.0 / 255, 1);
                            break;
                        case 12:
                            toFill = Color.color(100.0 / 255, 204.0 / 255, 1);
                            break;
                        case 21:
                            toFill = Color.color(1, 1, 153.0 / 255);
                            break;
                        case 31:
                            toFill = Color.color(1, 51.0 / 255, 51.0 / 255);
                            break;
                        default:
                            toFill = Color.GRAY;
                            break;
                    }
                }

                // drawing square of tile
                gc.setFill(toFill);
                gc.fillRect(col * square, row * square, square, square);
                gc.strokeRect(col * square, row * square, square, square);

                // drawing text of tile
                Tile t = b.getTileAt(row, col);     // tile on board
                if (this.isSquareWithPlayerPlacement(row, col)) {
                    t = this.tilesPlaced.get(this.PositionOnBoardToInt(row, col));
                    System.out.println("letter on the board\t[" + t.letter + "]");
                }

                if (t != null) {
                    gc.setFill(Color.BLACK); // Set the text color to black
                    gc.fillText("" + t.letter, (col + letterMargin) * square, (row + 1 - letterMargin) * square);
                }
            }
        }
    }

    public void drawTiles() {
        int square = (int) this.TilesCanvas.getHeight();
        this.squareOfTiles = square;
        this.TilesCanvas.heightProperty().set(square+0.1);
        GraphicsContext gc = this.TilesCanvas.getGraphicsContext2D();

        this.TilesCanvas.setWidth(square * (1 + this.tilePadding) * tilesInHand.size() - tilePadding*square*0.5); // adapt canvas width


        int i = 0;
        for (Tile t : tilesInHand) {
            double startX = i * (square * (1+tilePadding));
            gc.setFill(Color.GRAY);
            if (i == this.selectedTileIndex)
                gc.setFill(Color.PLUM);

            gc.fillRect(startX, 0, square, square);
            gc.strokeRect(startX, 0, square, square);
            gc.setFill(Color.BLACK); // Set the text color to black

            gc.setFont(Font.font("Arial", square * 0.6)); // big letter font
            gc.fillText("" + t.letter, startX + letterMargin * square, (1-letterMargin) * square);

            gc.setFont(Font.font("Arial", square * 0.2)); // small letter font
            double startXletter = startX + (1-letterMargin*0.6) * square;
            if (t.score > 9)
                startXletter -= letterMargin * 0.5  * square;
            gc.fillText("" + t.score, startXletter, (1-letterMargin*0.5) * square);

            i++;
        }
    }

    @FXML
    void clickedOnCanvasEvent(MouseEvent event){
        if (!isPlayerTurn.getValue()) {
            this.displayMSG(new guiMessage("It is not your turn yet!", Alert.AlertType.INFORMATION));
            return;
        }

        double x = event.getX();
        double y = event.getY();
        double square = 1;
        Canvas clickedCanvas = (Canvas) event.getTarget();
        if (clickedCanvas == this.boardCanvas) {
            square = this.squareOfBoard;
        } else if (clickedCanvas == this.TilesCanvas) {
            square = this.squareOfTiles * (1 + this.tilePadding);
        }

        int row = 0, col = 0;
        row = (int) (y / square);
        col = (int) (x / square);

        if (clickedCanvas == this.boardCanvas) {
            if (this.isValidBoardPosition(row, col))
                this.clickedOnBoard(row, col);
        } else if (clickedCanvas == this.TilesCanvas) {
            if (this.isValidTilesPosition(row, col))
                this.clickOnTiles(row, col);
        }
        this.drawCanvases();
    }

     /** @return true if the position on canvas is inside the board **/
    boolean isValidBoardPosition(int row, int col) {
        return 0 <= col && col < Board.COL_NUM && 0 <= row && row < Board.ROW_NUM;
    }

    /** @return true if the position on canvas is inside the Tiles bag **/
    boolean isValidTilesPosition(int row, int col) {
        return 0 <= col && col < this.tilesInHand.size() && row == 0;
    }

    boolean IsValidBoardChoice(int row, int col) {
        Tile t = this.gameBoardProperty.get().getTileAt(row, col);
        return t == null && !this.isSquareWithPlayerPlacement(row, col);

        //move to view model class ?
        //not hereeeee!!!!
        // todo
        //check if the position in legal from the board point of view
        //check if this choice consists with the last ones.
    }

    boolean isSquareWithPlayerPlacement(int row, int col) {
        return tilesPlaced.containsKey(PositionOnBoardToInt(row, col));
    }

    /** @return int value of a point on the board **/
    private int PositionOnBoardToInt(int row, int col) {
        return row * Board.ROW_NUM + col;
    }

    void clickedOnBoard(int row, int col){
        System.out.println("clickedOnBoard \t <" + row + ", " + col + "> \t selectedTileIndex=" + this.selectedTileIndex);

        if (!IsValidBoardChoice(row, col))
        if (this.isTilesSelected() && ) {
            this.displayMSG(new guiMessage("This position is invalid", Alert.AlertType.INFORMATION));
            return;
        } else if (!this.isTilesSelected()) {
            // retrieve tile from board to hand
            this.tilesInHand.add(this.)
        }

        // there is a legal choice of tile to put on the board
        if (this.selectedTileIndex >= 0) {
            this.selectedBoardRow = row;
            this.selectedBoardCol = col;
            SoundManager.singleton.playSound(SoundManager.SOUND_TILE_PRESSED);

            Tile tileToMove = tilesInHand.remove(selectedTileIndex);
            this.tilesPlaced.put(PositionOnBoardToInt(row, col), tileToMove);
            this.resetTileSelection(); // reset tiles selection
        } else {
            this.resetBoardSelection();
        }


        this.drawCanvases();
    }

    void clickOnTiles(int row, int col) {
        System.out.println("clickedOnTiles \t <" + row + ", " + col + "> \t selectedTileIndex=" + this.selectedTileIndex);

        if (this.isTilesSelected() && this.selectedTileIndex == col)
            this.resetTileSelection();
        else {
            this.resetBoardSelection();
            this.selectedTileIndex = col;
        }
        SoundManager.singleton.playSound(SoundManager.SOUND_TILE_PRESSED);

        this.drawCanvases();
    }

    private void resetTileSelection() {
        this.selectedTileIndex = -1;
    }

    private void resetBoardSelection() {
        this.selectedBoardRow = this.selectedBoardCol = -1;
    }

    public void test_AddTiles() {
        for(int i=0; i< 16; i++) {
            tilesInHand.add(i, new Tile((char) ('A' + (i%26)),i*2));
        }
    }

    private boolean isTilesSelected() {
        return this.isValidTilesPosition(0, this.selectedTileIndex);
    }

    private boolean isBoardSelected() {
        return this.isValidBoardPosition(this.selectedBoardRow, this.selectedBoardCol);
    }
}