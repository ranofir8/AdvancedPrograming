package ap.ex2.bookscrabble.model;

import ap.ex2.bookscrabble.model.host.HostGameModel;
import ap.ex2.bookscrabble.view.PlayerTableRow;
import ap.ex2.scrabble.Board;
import ap.ex2.scrabble.Tile;

import java.util.*;

public class GameData {

    double game_id;

    String HostName;
    Board board;

    List<String> players;

    List<Integer> scores;
    List<Tile[]> playersTiles;

    public GameData(double game_id, String hostName, Board board, List<String> players, List<Integer> scores, List<Tile[]> playersTiles) {
        this.game_id = game_id;
        HostName = hostName;
        this.board = board;
        this.players = players;
        this.scores = scores;
        this.playersTiles = playersTiles;
    }

    public GameData(HostGameModel hgm) {
        this.game_id = Math.random()*1000;
        this.board = hgm.getGameInstance().getGameBoard();
//        this.HostName = hgm
        this.players = hgm.getPlayersTurn();
        this.scores = prepareScores(hgm.getPlayerScoreList());
//        this.playersTiles = preparePlayerTiles(hgm.);
    }
    public List<Integer> prepareScores(List<PlayerTableRow> scores){
        //get scores from tha table in the same order of Players list
        List<Integer> score_list = new ArrayList<>();
        for (String name : players) {
            int score = -1000;
            for (PlayerTableRow playerTableRow : scores) {
                if (playerTableRow.nickname.get().equals(name)) {
                    score = playerTableRow.getScore();
                    break;
                }//assuming PlayerScoreList and getPlayersTurn have the same names (not in order).
            }
            score_list.add(score);
        }

        return score_list;

    }

    private List<Tile[]> preparePlayerTiles( ){

        List<Tile[]> PlayerTiles = new ArrayList<>();


        return playersTiles;

    }
}
