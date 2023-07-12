package ap.ex3.GameScrabbleServer;

import ap.ex3.GameScrabbleServer.Saves.GameSave;
import ap.ex3.GameScrabbleServer.Saves.PlayerSave;
import ap.ex3.GameScrabbleServer.db.GameNotFoundException;
import ap.ex3.GameScrabbleServer.db.InvalidHostException;

import java.util.List;

public interface GameServer {
    // returns game ID after being saved
    int saveNewGame(GameSave gs);

    // returns gameState of the loaded game
    GameSave loadExistingGame(int gameID, String hostName) throws GameNotFoundException, InvalidHostException;

    List<PlayerSave> loadScoresOfGame(int gameID) throws GameNotFoundException;
}
