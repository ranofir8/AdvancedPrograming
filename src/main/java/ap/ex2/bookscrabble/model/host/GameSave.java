package ap.ex2.bookscrabble.model.host;

import ap.ex2.scrabble.Board;
import ap.ex2.scrabble.Tile;
import org.hibernate.annotations.Entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


public class GameSave {
    private int gameID;
    private String hostName;
//    private final Board gameBoard;
    // ordered by turns
    private List<PlayerSave> listOfPlayers = new ArrayList<>();

    public int getGameID() {
        return gameID;
    }

    public String getHostName() {
        return hostName;
    }

//    public Board getGameBoard() {
//        return gameBoard;
//    }

    public List<PlayerSave> getListOfPlayers() {
        return listOfPlayers;
    }

    public GameSave(int gameID, String hostName, Board gameBoard, List<PlayerSave> listOfPlayers) {
        this.gameID = gameID;
        this.hostName = hostName;
        this.gameBoard = gameBoard;
        this.listOfPlayers = listOfPlayers;
    }

    public GameSave() {
    }

    private String convertToJSON() {
        // todo - from Moriya
        return "";
    }

    public void setGameID(int gameID) {
        this.gameID = gameID;
    }

    public void setHostName(String hostName) {
        this.hostName = hostName;
    }

    public void setListOfPlayers(List<PlayerSave> listOfPlayers) {
        this.listOfPlayers = listOfPlayers;
    }
}
