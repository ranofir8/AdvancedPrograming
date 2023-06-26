package ap.ex2.bookscrabble.model;

import ap.ex2.bookscrabble.common.ChangeBooleanProperty;
import ap.ex2.bookscrabble.view.PlayerTableRow;
import ap.ex2.scrabble.Board;
import ap.ex2.scrabble.Tile;
import ap.ex2.scrabble.Word;
import javafx.beans.property.*;
import javafx.beans.value.ObservableValue;

import java.util.*;
import java.util.stream.Collectors;

/*
All things related to the current running game
 */
public class GameInstance {
    public final ChangeBooleanProperty scoreBoardChangeEvent;     // get notified when score board changes
    public final ChangeBooleanProperty gameStatusChangeEvent;     // get notified when game/turn status changes
    public final ChangeBooleanProperty boardTilesChangeEvent;     // get notified when tiles/board/joined players changes
    private final ChangeBooleanProperty playerListChangeEvent;

    private final HashMap<String, Integer> scoreBoard;
    public BooleanProperty canStartGameProperty;
    private Board gameBoard;
    private Tile.Bag gameBag;

    private final ObjectProperty<GameState> gameStateProperty;
    private final PlayerStatus myPlayer;
    private String[] notLegalWords;

    private Map<String, Boolean> selectionMap; // True means the player has joined, False means the player has yet joined

    public Word limboToWord() {
        return this.getPlayerStatus().limboToWord(this.gameBag);
    }

    public boolean isTurnOf(String player) { //Gilad weird scenarios
        return (player == null && this.isMyTurn()) || (this.myPlayer.getTurnOfWho().equals(player));
    }

    public void thisIsAsavedGame() {
        this.gameStateProperty.set(GameState.WAITING_FOR_PLAYERS_GAME_SAVE);
    }

    enum GameState {
        WAITING_FOR_PLAYERS, WAITING_FOR_PLAYERS_GAME_SAVE, PLAYING, GAME_ENDED
    }

    public void setTurnOfNickname(String turnOfNickname) {
        this.myPlayer.setTurnOf(turnOfNickname);
    }

    /**
     *
     * @param nickName - make sure its valid
     * This class is for each player in order to update their view
     */
    public GameInstance(String nickName) {
        this.myPlayer = new PlayerStatus(nickName);
        this.scoreBoard = new HashMap<>();
        this.gameBoard = new Board();  // game not started yet
        this.scoreBoardChangeEvent = new ChangeBooleanProperty();
        this.gameStatusChangeEvent = new ChangeBooleanProperty();  // update on GameState and Turn change
        this.gameStateProperty = new SimpleObjectProperty<>();
        this.boardTilesChangeEvent = new ChangeBooleanProperty();
        this.playerListChangeEvent = new ChangeBooleanProperty();

        this.gameStatusChangeEvent.changeByProperty(this.gameStateProperty);
        this.myPlayer.bindToTilesChanged(this.boardTilesChangeEvent);
        this.myPlayer.bindToPlayerTurn(this.gameStatusChangeEvent);

        this.gameStateProperty.set(GameState.WAITING_FOR_PLAYERS);
        this.notLegalWords = null;
        this.playerListChangeEvent.addListener((observableValue, aBoolean, t1) -> {
            gameStatusChangeEvent.alertChanged();
            // todo update CHECK NUM OF PLAYERS < 4 AND IF WAITING FOR SELECTION LIST OR NOT
        });

        updateScoreBoard(nickName, 0);


    }

    /**
     * update scoreBoard (for view)
     * @param recentPlayerName - the player who played last and (maybe) achieved points
     * @param inc points achieved
     */
    public void updateScoreBoard(String recentPlayerName, int inc) {
        this.scoreBoard.putIfAbsent(recentPlayerName, 0);
        int lastScore = this.scoreBoard.get(recentPlayerName);
        this.scoreBoard.put(recentPlayerName, lastScore+inc);

        this.scoreBoardChangeEvent.alertChanged();
    }

    public void removeScoreBoardPlayer(String playerName) {
        this.scoreBoard.remove(playerName);

        this.scoreBoardChangeEvent.alertChanged();
    }

    public String getNickname() {return this.myPlayer.nickName;}

    public List<PlayerTableRow> getPlayerList() {
        return this.scoreBoard.entrySet().stream()
                .map(stringIntegerEntry -> new PlayerTableRow(
                        stringIntegerEntry.getKey() + (stringIntegerEntry.getKey().equals(myPlayer.nickName) ? " (You)" : ""),
                        stringIntegerEntry.getValue()))
                .collect(Collectors.toList());
    }

    public String getCurrentGameStatus() {
        switch (this.gameStateProperty.get()) {
            case WAITING_FOR_PLAYERS:
                return "Waiting for players to join";
            case WAITING_FOR_PLAYERS_GAME_SAVE:
                Set<String> waitingFor = new HashSet<>();
                this.selectionMap.forEach((name, val) -> {
                    if (!val)
                        waitingFor.add(name);
                });
                // test this todo
                return "Waiting for: " + waitingFor.stream().collect(Collectors.joining(", ")) + " to join..."; // todo

            case PLAYING:
                if (this.isMyTurn())
                    return "It's your turn!";
                else
                    return  this.getPlayerStatus().getTurnOfWho() + " is playing right now...";
            case GAME_ENDED:
                return "Game ended";
        }
        return "Unknown game state";
    }

    public boolean isMyTurn() {
        return this.myPlayer.getIsMyTurn();
    }

    public void onStartGame() {
        this.gameStateProperty.set(GameState.PLAYING);
        this.gameBoard = new Board();
        this.gameBag = new Tile.Bag();
        this.boardTilesChangeEvent.alertChanged();
    }

    public void setGameBoard(Board b) {
        this.gameBoard = b;
        this.boardTilesChangeEvent.alertChanged();
    }

    public String getWinner() {
        return Collections.max(this.scoreBoard.entrySet(), Map.Entry.comparingByValue()).getKey();
    }

    public PlayerStatus getPlayerStatus() {
        return this.myPlayer;
    }

    public Tile.Bag getGameBag() {
        return this.gameBag;
    }

    public Board getGameBoard() {
        return this.gameBoard;
    }

    public void setNotLegalWords(String[] notLegalWords) {
        this.notLegalWords = notLegalWords;
    }

    public String[] getNotLegalWords() {
        return notLegalWords;
    }

    public void setSelectionMap(Set<String> selectionSet) {
        selectionSet.forEach(name -> this.selectionMap.put(name, false));
    }

    public void playerHasJoined(String playerName) {
        this.selectionMap.put(playerName, true);
        this.playerListChangeEvent.alertChanged();
        // todo update property
    }
}
