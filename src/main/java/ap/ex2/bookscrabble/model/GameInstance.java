package ap.ex2.bookscrabble.model;

import ap.ex2.scrabble.Board;
import ap.ex2.scrabble.Word;

import java.util.HashMap;

/*
All things related to the current running game
 */
public class GameInstance {
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
    }

    /**
     * update scoreBoard (for view)
     * @param recentPlayerName - the player who played last and (maybe) achieved points
     * @param inc points achieved
     */
    private void updateScoreBoard(String recentPlayerName, int inc) {
        this.scoreBoard.putIfAbsent(recentPlayerName, 0);
        int lastScore = this.scoreBoard.get(recentPlayerName);
        this.scoreBoard.put(recentPlayerName,lastScore+inc);
    }

    /**
     *
     * @param recentPlayerName the player who played last and (maybe) achieved points
     * @param newWord the word he put
     */
    public void updateBoard(String recentPlayerName, Word newWord) {
        this.updateScoreBoard(recentPlayerName, this.gameBoard.tryPlaceWord(newWord));
    }
}
