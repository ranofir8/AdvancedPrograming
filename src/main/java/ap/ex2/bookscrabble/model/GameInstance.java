package ap.ex2.bookscrabble.model;

import ap.ex2.bookscrabble.common.ChangeBooleanProperty;
import ap.ex2.bookscrabble.view.GameView;
import ap.ex2.bookscrabble.view.PlayerRowView;
import ap.ex2.scrabble.Board;
import ap.ex2.scrabble.Tile;
import ap.ex2.scrabble.Word;
import javafx.beans.property.*;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/*
All things related to the current running game
 */
public class GameInstance {
    public ChangeBooleanProperty scoreBoardChangeEvent;
    public ChangeBooleanProperty gameStatusChangeEvent;
    private final HashMap<String, Integer> scoreBoard;
    public Board gameBoard;
    private final PlayerStatus myPlayer;
    private ObjectProperty<GameState> gameStateProperty;
    private Tile.Bag gameBag;
    private String[] notLegalWords;


    public Word limboToWord() {
        return this.getPlayerStatus().limboToWord(this.gameBag);
    }

    enum GameState {
        WAITING_FOR_PLAYERS, PLAYING, GAME_ENDED
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
        this.scoreBoardChangeEvent = new ChangeBooleanProperty();
        this.gameStatusChangeEvent = new ChangeBooleanProperty();  // update on GameState and Turn change
        this.gameStateProperty = new SimpleObjectProperty<>();

        this.gameStatusChangeEvent.changeByProperty(this.gameStateProperty);
        this.myPlayer.bindToPlayerTurn(this.gameStatusChangeEvent);

        this.gameStateProperty.set(GameState.WAITING_FOR_PLAYERS);
        this.notLegalWords = null;

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
        switch (this.gameStateProperty.get()) {
            case WAITING_FOR_PLAYERS:
                return "Waiting for players to join";
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
        System.out.println("game started");
        this.gameStateProperty.set(GameState.PLAYING);
        this.gameBoard = new Board();

        this.gameBag = new Tile.Bag();
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
}
