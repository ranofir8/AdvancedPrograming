package ap.ex3.GameScrabbleServer;

import ap.ex3.GameScrabbleServer.Saves.GameSave;

public interface GameServer {
    // returns game ID after being saved
    int saveNewGame(GameSave gs);

    // returns gameState of the loaded game
    GameSave loadExistingGame(int gameID, String hostName) throws GameNotFoundException, InvalidHostException;
}
