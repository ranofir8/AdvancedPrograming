package ap.ex2.GameScrabbleServer.Saves;

import ap.ex2.scrabble.Board;

import java.util.ArrayList;
import java.util.List;


public class GameSave {
    private int gameID;
    private String hostName;
    private String gameBoardString;
    // ordered by turns
    private List<PlayerSave> listOfPlayers = new ArrayList<>();

    public int getGameID() {
        return gameID;
    }

    public String getHostName() {
        return hostName;
    }

    public String getGameBoard() {
        return gameBoardString;
    }

    public List<PlayerSave> getListOfPlayers() {
        return listOfPlayers;
    }

    public GameSave(int gameID, String hostName, Board gameBoard, List<PlayerSave> listOfPlayers) {
        this.gameID = gameID;
        this.hostName = hostName;
        this.setGameBoard(gameBoard);
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

    public void setGameBoard(String gameBoard) {
        this.gameBoardString = gameBoard;
    }

    public void setGameBoard(Board gameBoard) {
        this.gameBoardString = gameBoard.summeryString();
    }
}
