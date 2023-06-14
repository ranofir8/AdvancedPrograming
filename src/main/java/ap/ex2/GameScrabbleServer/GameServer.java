package ap.ex2.GameScrabbleServer;

import ap.ex2.bookscrabble.model.host.GameSave;

public interface GameServer {
    // returns game ID after being saved
    int saveNewGame(GameSave gs);

    // returns gameState of the loaded game
    GameSave loadExistingGame(int gameID, String hostName) throws GameNotFoundException, InvalidHostException;
}
