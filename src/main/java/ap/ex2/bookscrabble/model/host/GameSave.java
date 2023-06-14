package ap.ex2.bookscrabble.model.host;

import ap.ex2.scrabble.Board;
import ap.ex2.scrabble.Tile;

import java.util.List;

public class GameSave {
    private final int gameID;
    private final String hostName;
    private final Board gameBoard;
    // ordered by turns
    private final List<PlayerSave> listOfPlayers;

    public GameSave(int gameID, String hostName, Board gameBoard, List<PlayerSave> listOfPlayers) {
        this.gameID = gameID;
        this.hostName = hostName;
        this.gameBoard = gameBoard;
        this.listOfPlayers = listOfPlayers;
    }

    private static class PlayerSave {  // saved player status: name, score, tiles in hand
        private String playerName;
        private int playerScore;
        private List<Tile> playerTiles;
    }

    private String convertToJSON {
        // todo - from Moriya
    }


}
