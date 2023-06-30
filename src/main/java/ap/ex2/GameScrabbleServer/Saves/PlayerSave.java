package ap.ex2.GameScrabbleServer.Saves;

import ap.ex2.scrabble.Tile;

import java.util.List;

public class PlayerSave {  // saved player status: name, score, tiles in hand
    private String playerName;
    private int playerScore;
    private String playerTiles;
    private Long playerId;


    public Long getPlayerId() {
        return playerId;
    }

    public void setPlayerId(Long playerId) {
        this.playerId = playerId;
    }

    public PlayerSave() {

    }

    public PlayerSave(String playerName, int playerScore, String playerTiles) {
        this.playerName = playerName;
        this.playerScore = playerScore;
        this.playerTiles = playerTiles;
    }

    public String getPlayerName() {
        return playerName;
    }

    public int getPlayerScore() {
        return playerScore;
    }

    public String getPlayerTiles() {
        return playerTiles;
    }

    public void setPlayerName(String playerName) {
        this.playerName = playerName;
    }

    public void setPlayerScore(int playerScore) {
        this.playerScore = playerScore;
    }

    public void setPlayerTiles(String playerTiles) {
        this.playerTiles = playerTiles;
    }
}