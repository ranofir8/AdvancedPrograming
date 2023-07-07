package ap.ex3.GameScrabbleServer;

import ap.ex3.GameScrabbleServer.Saves.GameSave;
import ap.ex3.GameScrabbleServer.Saves.test_GameSave;
import ap.ex3.GameScrabbleServer.db.GameNotFoundException;
import ap.ex3.GameScrabbleServer.db.InvalidHostException;
import org.junit.jupiter.api.Test;

class test_ScrabbleGameServer {

    @Test
    void saveNewGame() {
        ScrabbleGameServer server = ScrabbleGameServer.getInstance();
        GameSave gs = test_GameSave.createDummyObject();

        int r = server.saveNewGame(gs);
        System.out.println("Created new game with id: " + r);
    }

    @Test
    void loadExistingGame() {
        int gameID = 8291;
        String hostName = "hostGilad";
        ScrabbleGameServer server = ScrabbleGameServer.getInstance();

        try {
            GameSave gs = server.loadExistingGame(gameID, hostName);
        } catch (GameNotFoundException e) {
            System.out.println("Game ID " + gameID + " was not found.");
        } catch (InvalidHostException e) {
            System.out.println("Game ID " + gameID + " has a different host, therefore you cannot access it.");
        }
    }
}