package ap.ex2.bookscrabble.model;

import ap.ex2.bookscrabble.common.ChangeBooleanProperty;
import ap.ex2.bookscrabble.view.PlayerRowView;
import ap.ex2.scrabble.Board;
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
    public ChangeBooleanProperty scoreBoardChangeEvent;
    private HashMap<String, Integer> scoreBoard;
    private Board gameBoard;
    private PlayerStatus myPlayer;

    /**
     *
     * @param nickName - make sure its valid
     * This class is for each player in order to update their view
     */
    public GameInstance(String nickName) {
        this.gameBoard = new Board();
        this.myPlayer = new PlayerStatus(nickName);
        this.scoreBoard = new HashMap<>();
        this.scoreBoardChangeEvent = new ChangeBooleanProperty();

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
        this.updateScoreBoard(recentPlayerName, this.gameBoard.tryPlaceWord(newWord));
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

}
