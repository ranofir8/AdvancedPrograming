package ap.ex2.GameScrabbleServer;

import ap.ex2.GameScrabbleServer.Saves.GameSave;
import ap.ex2.GameScrabbleServer.Saves.test_GameSave;
import org.junit.jupiter.api.Test;

class ScrabbleGameServerTest {

    @Test
    void saveNewGame() {
        ScrabbleGameServer server = new ScrabbleGameServer();
        GameSave gs = test_GameSave.createDummyObject();

        int r = server.saveNewGame(gs);
        System.out.println("Created new game with id: " + r);
    }

    @Test
    void loadExistingGame() {
        int gameID = 8291;
        String hostName = "hostGilad";
        ScrabbleGameServer server = new ScrabbleGameServer();

        try {
            GameSave gs = server.loadExistingGame(gameID, hostName);
        } catch (GameNotFoundException e) {
            System.out.println("Game ID " + gameID + " was not found.");
        } catch (InvalidHostException e) {
            System.out.println("Game ID " + gameID + " has a different host, therefore you cannot access it.");
        }
    }
}