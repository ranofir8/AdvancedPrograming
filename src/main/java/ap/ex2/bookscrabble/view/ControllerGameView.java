package ap.ex2.bookscrabble.view;

import ap.ex2.bookscrabble.common.ChangeBooleanProperty;
import ap.ex2.bookscrabble.common.Command;
import ap.ex2.bookscrabble.common.guiMessage;
import ap.ex2.bookscrabble.model.GameInstance;
import ap.ex2.bookscrabble.model.GameModel;
import ap.ex2.bookscrabble.model.PlayerStatus;
import ap.ex2.scrabble.Board;
import ap.ex2.scrabble.Tile;
import javafx.application.Platform;
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
import java.util.stream.Collectors;

import static java.lang.System.exit;

public class ControllerGameView extends GameView implements Initializable {
    private final BooleanProperty isHostGame;

    private final IntegerProperty playersCount;
    private final ChangeBooleanProperty vBoardTilesChangedEvent;
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
    private HashMap<Integer, Tile> tilesPlacedLimbo;
    private PlayerStatus playerStatus;
    private int selectedBoardRow = -1;
    private int selectedBoardCol = -1;
    private int selectedTileIndex = -1;

    @FXML
    private Label portNum;
    @FXML
    private Label gameStatusLabel;
    @FXML
    private TableView<PlayerTableRow> scoreBoard;
    @FXML
    private ScrollPane tilesSP;
    @FXML
    private Button startGameButton;
    @FXML
    private Canvas tilesCanvas;
    @FXML
    private Canvas boardCanvas;
    @FXML
    Button skipTurnButton;
    @FXML
    Button sendWordButton;
    @FXML
    Button shuffleTiles;
    @FXML
    private Slider volumeSlider;


    public ControllerGameView() {
        this.isHostGame = new SimpleBooleanProperty();
        this.gameInstanceProperty = new SimpleObjectProperty<>();
        this.playersCount = new SimpleIntegerProperty();
        this.isPlayerTurn = new SimpleBooleanProperty();
        this.canSendWord = new SimpleBooleanProperty();
        this.vBoardTilesChangedEvent = new ChangeBooleanProperty();

        this.tilesInHand = new ArrayList<>();

        this.gameInstanceProperty.addListener((observableValue, g0, g1) -> {
            this.gameBoard = g1.getGameBoard();
            this.playerStatus = g1.getPlayerStatus();
            this.tilesInHand = this.playerStatus.getTilesInHand();
            this.tilesPlacedLimbo = this.playerStatus.getTilesInLimbo();

            this.isPlayerTurn.bind(g1.getPlayerStatus().isMyTurnProperty);
        });
        this.vBoardTilesChangedEvent.addListener((observableValue, aBoolean, t1) -> {
            Platform.runLater(() -> {
                this.drawCanvases();
                this.shuffleTiles.disableProperty().set(this.tilesInHand == null || this.tilesInHand.isEmpty());
                this.canSendWord.set(this.tilesPlacedLimbo != null && !this.tilesPlacedLimbo.isEmpty());
            });
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
        this.gameStatusLabel.textProperty().bind(this.myViewModel.gameStatusStringProperty);
        this.myViewModel.playerScoreboard.bind(this.scoreBoard.itemsProperty());
        this.playersCount.bind(this.myViewModel.countPlayers);

        this.playersCount.addListener((observableValue, n0, n1) -> this.startGameButton.setDisable(n1.intValue()< GameModel.MIN_PLAYERS));

        this.gameInstanceProperty.bind(this.myViewModel.gameInstanceProperty);

        this.sendWordButton.disableProperty().bind(this.canSendWord.not());
        this.skipTurnButton.disableProperty().bind(this.isPlayerTurn.not());
        this.isPlayerTurn.addListener((observableValue, b0, b1) -> {
            this.resetTileSelection();
            this.resetBoardSelection();
            if (b1)
                SoundManager.singleton.playSound(SoundManager.SOUND_YOUR_TURN);
        });

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
                    case UPDATE_GAME_CANVASES:
                        this.vBoardTilesChangedEvent.alertChanged();
                        break;
                    case PLAY_START_GAME_SOUND:
                        SoundManager.singleton.playSound(SoundManager.SOUND_STARTING_GAME);
                        break;
                    case SOUND_NEW_PLAYER_JOINED:
                        SoundManager.singleton.playSound(SoundManager.SOUND_PLAYER_JOINED, true);
                        break;
                    case SOUND_NEW_WORD:
                        SoundManager.singleton.playSound(SoundManager.SOUND_NEW_WORD);
                        break;
                    case UPDATE_SCORE_BOARD:
                        break;
                    case INVALID_WORD_PLACEMENT:
                        this.displayMSG(new guiMessage("Invalid tile placements", Alert.AlertType.ERROR));
                        break;

                    case DISPLAY_CHALLENGE_PROMPT:
                        Platform.runLater(this::displayChallengeAlert);
                        //this.displayChallengeAlert();
                        break;

                    case DISPLAY_WINNER:
                        SoundManager.singleton.playSound(SoundManager.SOUND_OF_WIN);
                        Platform.runLater(this::displayWinner);
                        break;
                }
            } else if (arg instanceof String[]) {
                String[] args = (String[]) arg;
                if (args[0].equals("CRASH"))
                    this.displayCrash(args[1]);
            }
        }
    }

    private void displayCrash(String msg) {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            SoundManager.singleton.playSound(SoundManager.SOUND_OF_FAILURE);
            alert.setTitle("Game crash");
            alert.setHeaderText("Oops!");
            alert.setContentText(msg);
            alert.showAndWait();
            exit(1);
        });
    }

    private void displayWinner() {
        this.gameStatusLabel.textProperty().unbind();
        String winner = this.gameInstanceProperty.get().getWinner();
        this.gameStatusLabel.textProperty().set("The winner is: " + winner);
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Game End");
        alert.setHeaderText("The game has come to an end!");
        alert.setContentText("The winner is: " + winner +", Congrats!");
        Optional<ButtonType> result = alert.showAndWait();
        exit(0);//bye bye
    }

    private void displayChallengeAlert() {
        ButtonType challengeBu = new ButtonType("Challenge", ButtonBar.ButtonData.YES);
        ButtonType skipBu = new ButtonType("Skip turn", ButtonBar.ButtonData.CANCEL_CLOSE);
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "", challengeBu, skipBu);

        alert.setTitle("Challenge Option");
        alert.setHeaderText("Do you want to challenge?");
        String[] illegal = this.myViewModel.gameInstanceProperty.get().getNotLegalWords();

        String illegalListString = Arrays.stream(illegal).map(x -> "\t* " + x).collect(Collectors.joining("\n"));

        alert.setContentText("The following word(s) you created were not found in the dictionary: \n" +
                illegalListString + "\n" +
                "Do you want to challenge the server with those words?\n\n" +
                "If wrong you will lose " + Math.abs(GameModel.MISS_CHALLENGE_PENALTY) + " points and be skipped, " +
                "But if you are correct, you will earn " + GameModel.HIT_CHALLENGE_BONUS + " points.");


        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get().getButtonData() == ButtonBar.ButtonData.YES) {
            // challenge
            this.myViewModel.requestChallenge();
        } else {
            // cancel option
            this.skipTurnAction();
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        this.initSceneBind();

//        this.tilesSP.minViewportWidthProperty().bind(this.TilesCanvas.widthProperty());
//        this.tilesSP.prefViewportWidthProperty().bind(this.TilesCanvas.widthProperty());

        this.tilesSP.prefViewportHeightProperty().bind(this.tilesCanvas.heightProperty());
        this.tilesSP.minViewportHeightProperty().bind(this.tilesCanvas.heightProperty());

        TableColumn<PlayerTableRow, String> nicknameCol = new TableColumn<PlayerTableRow,String>("Nickname");
        nicknameCol.setCellValueFactory(new PropertyValueFactory("Nickname"));
        TableColumn<PlayerTableRow, String> scoreCol = new TableColumn<PlayerTableRow,String>("Score");
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

        //this.drawGameBoard(); todo tetst%
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

        Board b = null;
        if (this.gameInstanceProperty.get() != null)
            b = this.gameInstanceProperty.get().getGameBoard();
        if (b == null)
            b = new Board(); // dummy board for drawing

        int w = (int) this.boardCanvas.getWidth(), h = (int) this.boardCanvas.getHeight();
        this.squareOfBoard = (int)(Math.min(w, h) / (float)Math.max(Board.ROW_NUM, Board.COL_NUM));
        double square = this.squareOfBoard;

        gc.setFont(Font.font("Arial", FontWeight.BOLD, square * 0.8)); // Adjust the font size as needed
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
                if (row == 7 && col ==7) // star position
                    drawStar((double)(col+0.5)*square, (double) (row+0.5)*square, (double) square /2);
                if (row == this.selectedBoardRow && col == this.selectedBoardCol) {
                    gc.setFill(boardSelectionBG);
                    gc.fillRect(col * square, row * square, square, square);
                }

                gc.strokeRect(col * square, row * square, square, square);


                if (this.tilesPlacedLimbo == null)
                    continue;
                // drawing text of tile
                boolean isTempTile = false;
                Tile t = b.getTileAt(row, col);     // tile on board
                if (this.isSquareWithPlayerPlacement(row, col)) {
                    t = this.tilesPlacedLimbo.get(Board.positionToInt(row, col));
                    isTempTile = true;
                }

                if (t != null) {
                    gc.setFill(Color.BLACK); // Set the text color to black
                    if (isTempTile)
                        gc.setFill(tempTextColor);
                    gc.fillText(String.valueOf(t.letter), (col + letterMargin-0.1) * square, (row + 1 - letterMargin) * square);
                }
            }
        }
    }

    /**
     *t the function draws a star on the board in the locetion provided.
     */
    private void drawStar(double x, double y, double size) {

        GraphicsContext gc = this.boardCanvas.getGraphicsContext2D();
        double[] xPoints = new double[10];
        double[] yPoints = new double[10];
        double innerRadius = size / 2.5;
        double outerRadius = size;

        for (int i = 0; i < 10; i++) {
            double radius = (i % 2 == 0) ? outerRadius : innerRadius;
            double angle = Math.PI * i / 5 - Math.PI / 2;
            xPoints[i] = x + radius * Math.cos(angle);
            yPoints[i] = y + radius * Math.sin(angle);
        }

        gc.setFill(Color.ORANGERED);
        gc.fillPolygon(xPoints, yPoints, 10);

//        // Draw the outline of the star
//        gc.setStroke(Color.SILVER);
//        gc.setLineWidth(2);
//        gc.strokePolygon(xPoints, yPoints, 10);

        gc.setStroke(Color.BLACK); // get back the black color


    }

    public void drawTiles() {
        int square = (int) this.tilesCanvas.getHeight();
        this.squareOfTiles = square;
        this.tilesCanvas.heightProperty().set(square+0.1);
        GraphicsContext gc = this.tilesCanvas.getGraphicsContext2D();

        this.tilesCanvas.setWidth(square * (1 + this.tilePadding) * tilesInHand.size() - tilePadding*square*0.5); // adapt canvas width

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
        } else if (clickedCanvas == this.tilesCanvas) {
            square = this.squareOfTiles * (1 + this.tilePadding);
        }

        int row = 0, col = 0;
        row = (int) (y / square);
        col = (int) (x / square);

        if (clickedCanvas == this.boardCanvas) {
            if (this.isValidBoardPosition(row, col))
                this.clickedOnBoard(row, col);
        } else if (clickedCanvas == this.tilesCanvas) {
            if (this.isValidTilesPosition(row, col))
                this.clickOnTiles(row, col);
        }
        this.drawCanvases();
    }

    @FXML
    public void shuffleTilesAction() {
        this.playerStatus.shuffleTiles();
        SoundManager.singleton.playSound(SoundManager.SOUND_TILE_SHUFFLE, true);
    }

    @FXML
    private void ShowInstructions() {
        this.displayMSG(new guiMessage("Welcome again. You're probably familiar with the game rules but here's a" +
                " quick reminder :)\n\n" +
        "The game is played in rounds of turns. After turn order is determined randomly, the game starts. During his" +
                " turn, a player is able to put tiles on empty squares. After doing so correctly, meaning put a tile " +
                "or tiles which are all in the same row or column, such that all new words created from up to down" +
                " and from left to right are valid by dictionary, score will be given to that player based on those" +
                " new words and any special squares they cover.\n\n" +
        "Notice, at the start of the game the first player should put a legal word which in it one of the tiles" +
                " covers on the start square. If he isn't able to do so he can pass his turn, and the next player" +
                " must cover a star/pass and so on until the star is covered.\n\n" +
        "Notice 2, if a player thinks the fast dictionary algorithm didn't detect correctly a valid word, they" +
                " may challenge the dictionary with a slower but more precised algorithm. If this challenge failed," +
                " the player will lose points!\n\n" +
        "GOOD LUCK, ENJOY!", Alert.AlertType.INFORMATION, "Instructions"));
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
        return tilesPlacedLimbo.containsKey(Board.positionToInt(row, col));
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

                SoundManager.singleton.playSound(SoundManager.SOUND_TILE_PRESSED, true);
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

        SoundManager.singleton.playSound(SoundManager.SOUND_TILE_PRESSED, true);
    }

    @FXML
    void sendWordAction() {
        // model -> chack logic and known word, update boards , change player turn... continue
        this.myViewModel.sendWord();
    }

    @FXML
    void skipTurnAction() {
        this.myViewModel.giveUpTurn();
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