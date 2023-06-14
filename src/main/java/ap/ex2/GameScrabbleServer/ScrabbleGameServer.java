package ap.ex2.GameScrabbleServer;

import ap.ex2.bookscrabble.model.host.GameSave;

public class ScrabbleGameServer implements GameServer {
    private HTTPServerManager httpServerManager; // the server of the http. we get from it actions

    @Override
    public int saveNewGame(GameSave gs) {
        // todo
        return 0;
    }

    // when a save HTTP request is coming, this method will be called
    @Override
    public GameSave loadExistingGame(int gameID, String hostName) throws GameNotFoundException, InvalidHostException {
        // todo
        return null;
    }
}
