package ap.ex2.bookscrabble.view;

import ap.ex2.bookscrabble.Config;
import ap.ex2.bookscrabble.common.Command;
import ap.ex2.bookscrabble.common.guiMessage;
import ap.ex2.bookscrabble.model.GameInstance;
import ap.ex2.bookscrabble.model.GameModel;
import ap.ex2.bookscrabble.model.PlayerStatus;
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
import javafx.scene.text.FontWeight;

import java.net.URL;
import java.util.*;

public class ControllerGameView extends GameView implements Initializable {
    private final BooleanProperty isHostGame;

    private final IntegerProperty playersCount;
    private final BooleanProperty isPlayerTurn;
    private final BooleanProperty canSendWord;

    // board & tiles
    private List<Tile> tilesInHand;
    private Board gameBoard;
    private final ObjectProperty<GameInstance> gameInstanceProperty;

    // tile drawing
    private final double letterMargin = 0.25;
    private final double tilePadding = 0.1;
    private double squareOfBoard = 1;
    private double squareOfTiles = 1;
    private static final Color boardSelectionBG = Color.color(1, 1, 1, 0.5);
    private static final Color tempTextColor = Color.color(1, 170.0/255, 200.0/255);

    // board selections
    private HashMap<Integer, Tile> tilesPlaced;
    private PlayerStatus playerStatus;
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
    @FXML
    Button skipTurnButton;
    @FXML
    Button sendWordButton;
    @FXML
    private Slider volumeSlider;


    public ControllerGameView() {
        this.isHostGame = new SimpleBooleanProperty();
        this.gameInstanceProperty = new SimpleObjectProperty<>();
        this.playersCount = new SimpleIntegerProperty();
        this.isPlayerTurn = new SimpleBooleanProperty();
        this.canSendWord = new SimpleBooleanProperty();

        this.tilesInHand = new ArrayList<>();

        this.gameInstanceProperty.addListener((observableValue, g0, g1) -> {
            this.gameBoard = g1.getGameBoard();
            this.playerStatus = g1.getPlayerStatus();
            this.tilesInHand = this.playerStatus.getTilesInHand();
            this.tilesPlaced = this.playerStatus.getTilesInLimbo();

            this.isPlayerTurn.bind(g1.getPlayerStatus().isMyTurnProperty);

        });
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

        this.gameInstanceProperty.bind(this.myViewModel.gameInstanceProperty);
        this.myViewModel.gameInstanceProperty.addListener((observableValue, board, t1) -> System.out.println("gameInstance (Board) in V updated"));

        this.sendWordButton.disableProperty().bind(this.canSendWord.not());
        this.skipTurnButton.disableProperty().bind(this.canSendWord);

        SoundManager.singleton.bindMasterVolumeTo(this.volumeSlider.valueProperty());
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
                        this.drawTiles();
                        break;
                    case UPDATE_GAME_TILES:
                        this.drawTiles();
                        SoundManager.singleton.playSound(SoundManager.SOUND_TILE_ADD);
                        break;
                    case PLAY_START_GAME_SOUND:
                        SoundManager.singleton.playSound(SoundManager.SOUND_STARTING_GAME);
                        break;
                    case SOUND_NEW_PLAYER_JOINED:
                        SoundManager.singleton.playSound(SoundManager.SOUND_PLAYER_JOINED);
                        break;
                    case SOUND_NEW_WORD:
                        if (!this.isPlayerTurn.get())
                            SoundManager.singleton.playSound(SoundManager.SOUND_NEW_WORD);
                        break;
                    case UPDATE_SCORE_BOARD:
                        break;
                    case INVALID_WORD_PLACEMENT:
                        displayMSG(new guiMessage("Invalid tile placements", Alert.AlertType.ERROR));
                        SoundManager.singleton.playSound(SoundManager.SOUND_OF_FAILURE);
                        break;
                    case RESET_SELECTIONS:
                        resetBoardSelection();
                        resetTileSelection();
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

        Timer timer = new Timer("pop starter");
        TimerTask tt = new TimerTask() {
            @Override
            public void run() {
                SoundManager.singleton.enablePopSound();
            }
        };
        timer.schedule(tt, 100L);
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

        Board b = this.gameInstanceProperty.get().getGameBoard();

        int w = (int) this.boardCanvas.getWidth(), h = (int) this.boardCanvas.getHeight();
        this.squareOfBoard = (int)(Math.min(w, h) / (float)Math.max(Board.ROW_NUM, Board.COL_NUM));
        double square = this.squareOfBoard;

        gc.setFont(Font.font("Arial", FontWeight.BOLD, square * 0.6)); // Adjust the font size as needed
        for (int row = 0; row < Board.ROW_NUM; row++) {
            for (int col = 0; col < Board.COL_NUM; col++) {
                int m = b.getMultiplierAtInt(row, col);
                Color toFill;

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

                // drawing square of tile
                gc.setFill(toFill);
                gc.fillRect(col * square, row * square, square, square);
                if (row == this.selectedBoardRow && col == this.selectedBoardCol) {
                    gc.setFill(boardSelectionBG);
                    gc.fillRect(col * square, row * square, square, square);
                }

                gc.strokeRect(col * square, row * square, square, square);

                // drawing text of tile
                boolean isTempTile = false;
                Tile t = b.getTileAt(row, col);     // tile on board
                if (this.isSquareWithPlayerPlacement(row, col)) {
                    t = this.tilesPlaced.get(Board.positionToInt(row, col));
                    isTempTile = true;
                }

                if (t != null) {

                    gc.setFill(Color.BLACK); // Set the text color to black
                    if (isTempTile)
                        gc.setFill(tempTextColor);
                    gc.fillText(String.valueOf(t.letter), (col + letterMargin) * square, (row + 1 - letterMargin) * square);
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
        for (Tile t : this.tilesInHand) {
            double startX = i * (square * (1+tilePadding));
            gc.setFill(Color.GRAY);
            if (i == this.selectedTileIndex)
                gc.setFill(Color.PLUM);

            gc.fillRect(startX, 0, square, square);
            gc.strokeRect(startX, 0, square, square);
            gc.setFill(Color.BLACK); // Set the text color to black

            gc.setFont(Font.font("Arial", square * 0.6)); // big letter font
            gc.fillText(String.valueOf(t.letter), startX + letterMargin * square, (1-letterMargin) * square);

            gc.setFont(Font.font("Arial", square * 0.2)); // small letter font
            double startXletter = startX + (1-letterMargin*0.6) * square;
            if (t.score > 9)
                startXletter -= letterMargin * 0.5  * square;
            gc.fillText(String.valueOf(t.score), startXletter, (1-letterMargin*0.5) * square);

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

        this.canSendWord.set(!this.tilesPlaced.isEmpty());
    }

     /** @return true if the position on canvas is inside the board **/
    boolean isValidBoardPosition(int row, int col) {
        return 0 <= col && col < Board.COL_NUM && 0 <= row && row < Board.ROW_NUM;
    }

    /** @return true if the position on canvas is inside the Tiles bag **/
    boolean isValidTilesPosition(int row, int col) {
        return 0 <= col && col < this.tilesInHand.size() && row == 0;
    }

    boolean isValidBoardChoice(int row, int col) {
        Tile t = this.gameInstanceProperty.get().getGameBoard().getTileAt(row, col);
        return t == null && !this.isSquareWithPlayerPlacement(row, col);

        //move to view model class ?
        //not hereeeee!!!!
        // todo
        //check if the position in legal from the board point of view
        //check if this choice consists with the last ones.
    }

    private boolean isSquareWithPlayerPlacement(int row, int col) {
        return tilesPlaced.containsKey(Board.positionToInt(row, col));
    }

    void clickedOnBoard(int row, int col){
//        System.out.println("clickedOnBoard \t <" + row + ", " + col + "> \t selectedTileIndex=" + this.selectedTileIndex);
        if (this.isTilesSelected() && !isValidBoardChoice(row, col)) { // the player chose a tile but the placment is invalid
            this.displayMSG(new guiMessage("This position is invalid", Alert.AlertType.INFORMATION));
            System.out.println("the player chose a tile but the placment is invalid");
            return;
        } else if (!this.isTilesSelected()) { // the board position is ok but there isn't a selected tile .
            // if there is a tile in the board from this turn, retrieve tile from board

            if (this.isSquareWithPlayerPlacement(row, col)) {
                this.playerStatus.moveLimboToHand(Board.positionToInt(row, col));

                SoundManager.singleton.playSound(SoundManager.SOUND_TILE_ADD);
            }
            resetBoardSelection();
        } else {
            // there is a legal choice of tile to put on the board
            if (this.selectedTileIndex >= 0) {
                this.selectedBoardRow = row;
                this.selectedBoardCol = col;

                SoundManager.singleton.playSound(SoundManager.SOUND_TILE_PRESSED);
                this.playerStatus.moveHandToLimbo(this.selectedTileIndex, row, col);

                this.resetTileSelection();
            } else {
                this.resetBoardSelection();
            }
        }

        this.drawCanvases();
    }

    void clickOnTiles(int row, int col) {
//        System.out.println("clickedOnTiles \t <" + row + ", " + col + "> \t selectedTileIndex=" + this.selectedTileIndex);

        if (this.isTilesSelected() && this.selectedTileIndex == col)
            this.resetTileSelection();
        else {
            this.resetBoardSelection();
            this.selectedTileIndex = col;
        }

        SoundManager.singleton.playSound(SoundManager.SOUND_TILE_PRESSED);

        this.drawCanvases();
    }

    @FXML
    void sendWordAction() {

        //validate the choices are construct a word.
        //send to model. todo
        this.myViewModel.sendWord();
        //model -> chack logic and known word, update boards , change player turn... continue
    }

    @FXML
    void skipTurnAction() {
        // todo
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