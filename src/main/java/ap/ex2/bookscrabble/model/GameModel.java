package ap.ex2.bookscrabble.model;

import ap.ex2.bookscrabble.common.Command;
import ap.ex2.bookscrabble.common.Command2VM;
import ap.ex2.bookscrabble.common.Protocol;
import ap.ex2.bookscrabble.view.PlayerTableRow;
import ap.ex2.bookscrabble.view.SoundManager;
import ap.ex2.scrabble.Board;
import ap.ex2.scrabble.Tile;
import ap.ex2.scrabble.Word;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;

import java.util.List;

public abstract class GameModel extends Model {
    public static final int MIN_PLAYERS = 2;
    public static final int MAX_PLAYERS = 4;
    public static final int DRAW_START_AMOUNT = 7;
    public static final int MISS_CHALLENGE_PENALTY = -5;
    public static final int HIT_CHALLENGE_BONUS = 5;
    public final ObjectProperty<GameInstance> gameInstanceProperty;

    public GameModel(String nickname) {
        this.gameInstanceProperty = new SimpleObjectProperty<>();
        this.gameInstanceProperty.set(new GameInstance(nickname));

        this.gameInstanceProperty.get().gameStatusChangeEvent.addListener((observableValue, gameInstance, t1) -> notifyViewModel(new Command2VM(Command.UPDATE_GAME_STATUS_TEXT)));
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

            case Protocol.GAME_CRASH_ERROR:
                this.onGameCrash(msgExtra);
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
                this.onWordNotLegal(msgExtra.split(","));
                break;

            case Protocol.BOARD_ASSIGNMENT_ACCEPTED:
                this.onBoardAssignmentAccepted();
                break;

            case Protocol.BOARD_UPDATED_BY_ANOTHER_PLAYER:
                this.onBoardUpdateByPlayer(msgExtra);
                break;

            case Protocol.UPDATED_PLAYER_SCORE:
                String[] extraArgs = msgExtra.split(",", 2);
                if (extraArgs.length != 2)  // error, not valid
                    return false;
                int scoreOfWord = Integer.parseInt(extraArgs[0]);
                String player = extraArgs[1];
                this.onUpdatePlayerScore(scoreOfWord, player);
                break;

            case Protocol.BOARD_ASSIGNMENT_ACCEPTED_CHALLENGE:
                this.onChallengeAccepted();
                break;

            case Protocol.BOARD_ASSIGNMENT_REJECTED_CHALLENGE:
                this.onChallengeRejected();
                break;

            case Protocol.END_GAME_TILE_SUM_REQUEST:
                this.onEndGameTileSumRequest();
                break;

            case Protocol.END_GAME_WINNER:
                this.onGameWinner();
                break;

            default:
                return false; // not recognized
        }
        return true;
    }

    protected void onGameCrash(String player) {
        notifyViewModel(new String[]{"CRASH", "The game was ended because " + player + " left the game unexpectedly."});
        System.out.println("Crash error! closing connection...");
        this.closeConnection();
    }

    private void onGameWinner() {
        notifyViewModel(Command.DISPLAY_WINNER);
        this.closeConnection();
    }

    private void onChallengeAccepted() {
        notifyViewModel(new String[]{"MSG", "Your challenge attempt is accepted, I'm sorry for the confusion :("});
        SoundManager.singleton.playSound(SoundManager.SOUND_OF_APPROVAL);
        // send word again (now it will be accepted)
        this.requestSendWord();
    }

    private void onChallengeRejected() {
        notifyViewModel(new String[]{"MSG", "Your challenge attempt is rejected, you wasted the server's time!"});
        SoundManager.singleton.playSound(SoundManager.SOUND_OF_REJECTED);
    }

    private void onWordNotLegal(String[] illeagalWords) {
        this.gameInstanceProperty.get().setNotLegalWords(illeagalWords);
        notifyViewModel(Command.DISPLAY_CHALLENGE_PROMPT);
    }

    public String[] getNotLegalWords() {
        String[] a = this.getGameInstance().getNotLegalWords();
        if (a == null)
            return new String[0];
        return a;
    }

    protected void onBoardUpdateByPlayer(String wordPlaced) {
//        notifyViewModel(Command.UPDATE_GAME_BOARD);
        notifyViewModel(Command.SOUND_NEW_WORD);
        this.getGameInstance().boardTilesChangeEvent.alertChanged();
    }

    /**
     * update for a player when a legal word was set on board
     */
    private void onBoardAssignmentAccepted() {
        //remove relevant tiles from hand
        this.getGameInstance().getPlayerStatus().removeTilesInLimbo();
        // this.notifyViewModel(Command.RESET_SELECTIONS); // no need b.c turn of is happening next
        //put the tiles on the board - on turn of
    }

    private void onGotNewTiles(String tilesGotten) {
        for (char tileLetter : tilesGotten.toCharArray()) {
            // take from Bag and add to hand, at the end update board in GUI
            this.getGameInstance().getPlayerStatus().addTile(this._onGotNewTilesHelper(tileLetter));
        }
        SoundManager.singleton.playSound(SoundManager.SOUND_TILE_ADD);
    }

    protected abstract Tile _onGotNewTilesHelper(char tileLetter);




    public List<PlayerTableRow> getPlayerScoreList() {
        return this.getGameInstance().getPlayerScoreList();
    }

    protected void onNewPlayer(String newPlayerName) {
        this.onUpdatePlayerScore(0, newPlayerName);
        notifyViewModel(Command.SOUND_NEW_PLAYER_JOINED);
    }

    protected void onUpdatePlayerScore(int score, String player) {
        this.getGameInstance().updateScoreBoard(player, score);
    }

    protected void onStartGame() {
        this.getGameInstance().onStartGame();
        notifyViewModel(Command.PLAY_START_GAME_SOUND);
    }

    public void onTurnOf(String turnOfNickname) {
        this.getGameInstance().setTurnOfNickname(turnOfNickname);
        this.getGameInstance().getPlayerStatus().putBackTilesFromLimbo();
    }

    protected void onEndGameTileSumRequest() {
        this.sendMsgToHost(Protocol.END_GAME_TILE_SUM_RESPONSE + "" + this.getGameInstance().getPlayerStatus().getSumOfTiles());
    }

    public final void requestSendWord() {
        Word w = this.getGameInstance().limboToWord();  // not removing from bag
        if (w == null)
            notifyViewModel(Command.INVALID_WORD_PLACEMENT);
        else
            sendMsgToHost(Protocol.BOARD_ASSIGNMENT_REQUEST + w.toNetworkString());
    }

    public void requestChallenge() {
        sendMsgToHost(String.valueOf(Protocol.BOARD_CHALLENGE_REQUEST));
    }

    public void requestGiveUpTurn() {
        sendMsgToHost(String.valueOf(Protocol.SKIP_TURN_REQUEST));
    }

}
