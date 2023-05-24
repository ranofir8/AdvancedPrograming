package ap.ex2.bookscrabble.model;

import ap.ex2.bookscrabble.common.Command;
import ap.ex2.bookscrabble.common.Protocol;
import ap.ex2.bookscrabble.view.PlayerRowView;
import ap.ex2.scrabble.Tile;
import ap.ex2.scrabble.Word;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import java.util.List;

public abstract class GameModel extends Model {
    public static final int MIN_PLAYERS = 2;
    public static final int MAX_PLAYERS = 4;
    public static final int DRAW_START_AMOUNT = 7;
    private static final int MISS_PENALTY = 600;
    public ObjectProperty<GameInstance> gameInstanceProperty;

    public GameModel(String nickname) {
        this.gameInstanceProperty = new SimpleObjectProperty<>();
        this.gameInstanceProperty.set(new GameInstance(nickname));
    }

    public GameInstance getGameInstance() {
        return this.gameInstanceProperty.get();
    }

    public abstract int getDisplayPort();

    public abstract void establishConnection() throws Exception;

    protected abstract void closeConnection();

    protected abstract void sendMsgToHost(String msg);

    protected void onRecvMessage(String sentBy, String msgContent) {
        // nickname will always be null, because only the host can send message to guests
        char msgProtocol = msgContent.charAt(0); //when a message is sent, the first part is a protocol
        String msgExtra = msgContent.substring(1); //rest of the message contain details

        this.handleProtocolMsg(sentBy, msgProtocol, msgExtra);
    }

    protected boolean handleProtocolMsg(String msgSentBy, char msgProtocol, String msgExtra) {
        // common stuff for server & guest to do
        switch (msgProtocol) {
            case Protocol.START_GAME:
                this.onStartGame();
                break;
            case Protocol.TURN_OF:
                this.onTurnOf(msgExtra);
                break;
            case Protocol.SEND_NEW_TILES:
                this.onGotNewTiles(msgExtra); //msgExtra contains the tiles
                break;
            case Protocol.ERROR_OUTSIDE_BOARD_LIMITS:
                notifyViewModel(new String[]{"ERR", "Your word is outside the board's limits. Try again."});
                break;
            case Protocol.ERROR_NOT_ON_STAR:
                notifyViewModel(new String[]{"ERR", "The first word must be on the star tile. Try again."});
                break;     //cursor parking: |   |
            case Protocol.ERROR_NOT_LEANS_ON_EXISTING_TILES:
                notifyViewModel(new String[]{"ERR", "Your word does not lean on existing tiles. Try again."});
                break;
            case Protocol.ERROR_NOT_MATCH_BOARD:
                notifyViewModel(new String[]{"ERR", "Your word does not match the board's state. Try again."});
                break;
            case Protocol.ERROR_WORD_NOT_LEGAL:
                notifyViewModel(new String[]{"ERR", "The word you created isn't found in the dictionary. Do you want to challenge it? (if wrong you will lose " + GameModel.MISS_PENALTY + " points)."});
                break;
            case Protocol.BOARD_ASSIGNMENT_ACCEPTED:
                this.onBoardAssignmentAccepted();
                break;
            case Protocol.BOARD_UPDATED_BY_ANOTHER_PLAYER:
                this.onBoardUpdateByPlayer(msgExtra);
                break;
            default:
                return false; // not recognized
        }
        return true;
    }

    private void onBoardUpdateByPlayer(String wordPlaced) {
        Word w = Word.getWordFromNetworkString(wordPlaced, this.getGameInstance().getGameBag()); // not removing tiles from bag
        int expectedScore = this.getGameInstance().gameBoard.tryPlaceWord(w);
        System.out.println("Expected score of: "+expectedScore);
        notifyViewModel(Command.UPDATE_GAME_BOARD);
    }

    /**
     * update for a player when a legal word was set on board
     */
    private void onBoardAssignmentAccepted() {
        //remove relevant tiles from hand
        this.getGameInstance().getPlayerStatus().removeTilesInLimbo();
        //put the tiles on the board:

        //update
    }

    private void onGotNewTiles(String tilesGotten) {
        for (char tileLetter : tilesGotten.toCharArray()) {
            // take from Bag and add to hand, at the end update board in GUI
            this.getGameInstance().getPlayerStatus().addTile(this._onGotNewTilesHelper(tileLetter));
        }
        notifyViewModel(Command.UPDATE_GAME_TILES);
    }

    protected abstract Tile _onGotNewTilesHelper(char tileLetter);




    public List<PlayerRowView> getPlayerList() {
        return this.getGameInstance().getPlayerList();
    }

    protected void onNewPlayer(String newPlayerName) {
        this.getGameInstance().updateScoreBoard(newPlayerName, 0);
        notifyViewModel(Command.NEW_PLAYER_JOINED);
    }

    public void onStartGame() {
        this.getGameInstance().onStartGame();
        notifyViewModel(Command.PLAY_START_GAME_SOUND);
        notifyViewModel(Command.UPDATE_GAME_BOARD);
    }

    public void onTurnOf(String turnOfNickname){
        this.getGameInstance().setTurnOfNickname(turnOfNickname);
    }

    public final void requestSendWord() {
        Word w = this.getGameInstance().limboToWord();  // not removing from bag
        if (w == null)
            notifyViewModel(Command.INVALID_WORD_PLACEMENT);
        else
            sendMsgToHost(Protocol.BOARD_ASSIGNMENT_REQUEST + w.toNetworkString());
    }

    public void requestChallenge() {
        // todo
    }

    public void requestGiveUpTurn() {
    }
}
