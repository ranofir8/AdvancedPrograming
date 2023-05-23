package ap.ex2.bookscrabble.model;

import ap.ex2.bookscrabble.common.ChangeBooleanProperty;
import ap.ex2.bookscrabble.view.PlayerRowView;
import ap.ex2.scrabble.Board;
import ap.ex2.scrabble.Tile;
import ap.ex2.scrabble.Word;
import javafx.beans.InvalidationListener;
import javafx.beans.property.*;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;

import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

/*
All things related to the current running game
 */
public class GameInstance {


    enum GameState {
        WAITING_FOR_PLAYERS, PLAYING, GAME_ENDED
    }

    public ChangeBooleanProperty scoreBoardChangeEvent;
    private final HashMap<String, Integer> scoreBoard;
    public Board gameBoard;
    private final PlayerStatus myPlayer;
    private GameState gameState;
    private Tile.Bag gameBag;

    public void setTurnOfNickname(String turnOfNickname) {
        this.myPlayer.turnOfProperty.set(turnOfNickname);
    }

    /**
     *
     * @param nickName - make sure its valid
     * This class is for each player in order to update their view
     */
    public GameInstance(String nickName) {
        this.myPlayer = new PlayerStatus(nickName);
        this.scoreBoard = new HashMap<>();
        this.scoreBoardChangeEvent = new ChangeBooleanProperty();
        this.gameState = GameState.WAITING_FOR_PLAYERS;

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

    /**
     *
     * @param recentPlayerName the player who played last and (maybe) achieved points
     * @param newWord the word he put
     */
    public void updateGameBoard(String recentPlayerName, Word newWord) {
        this.updateScoreBoard(recentPlayerName, this.getGameBoard().tryPlaceWord(newWord));
    }

    public String getNickname() {return this.myPlayer.nickName;}

    // todo maybe dont create a new list each time, but update existing one each time this.gameBoard changes?
    public List<PlayerRowView> getPlayerList() {
        return this.scoreBoard.entrySet().stream()
                .map(stringIntegerEntry -> new PlayerRowView(
                        stringIntegerEntry.getKey() + (stringIntegerEntry.getKey().equals(myPlayer.nickName) ? " (You)" : ""),
                        stringIntegerEntry.getValue()))
                .collect(Collectors.toList());
    }

    public String getCurrentGameStatus() {
        switch (this.gameState) {
            case WAITING_FOR_PLAYERS:
                return "Waiting for players to join";
            case PLAYING:
                 return this.isMyTurn() ? "It's your turn":"somebody else is playing";
            case GAME_ENDED:
                return "Game ended";
        }
        return "Unknown game state";
    }

    public boolean isMyTurn() {
        return this.myPlayer.getIsMyTurn();
    }

    public void onStartGame() {
        this.gameState = GameState.PLAYING;
        this.gameBoard = new Board();
        this.gameBag = new Tile.Bag();
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

}
