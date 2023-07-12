package ap.ex3.GameScrabbleServer.Saves;

import ap.ex2.scrabbleParts.Board;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import com.google.gson.Gson;



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

    public GameSave(String hostName, Board gameBoard, List<PlayerSave> listOfPlayers) {
        this(-1, hostName, gameBoard, listOfPlayers);
    }

    public String convertToJSON() {
        Gson gson = new Gson();
        return gson.toJson(this);
    }

    public static GameSave convertFromJSON(String gameGson) {
        Gson gson = new Gson();
        return gson.fromJson(gameGson , GameSave.class);
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

    // returns player in this game as a set of strings
    public Set<String> getSelectionSet() {
        return this.getListOfPlayers().stream().map(PlayerSave::getPlayerName).collect(Collectors.toSet());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        GameSave gameSave = (GameSave) o;

        if (gameID != gameSave.gameID) return false;
        if (!hostName.equals(gameSave.hostName)) return false;
        if (!gameBoardString.equals(gameSave.gameBoardString)) return false;
        return listOfPlayers.equals(gameSave.listOfPlayers);
    }

}
