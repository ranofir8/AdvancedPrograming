package ap.ex2.GameScrabbleServer;

import ap.ex2.bookscrabble.model.host.GameSave;
import ap.ex2.bookscrabble.model.host.PlayerSave;
import ap.ex2.bookscrabble.model.host.test_GameSaveTest;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ScrabbleGameServerTest {

    @Test
    void saveNewGame() {
        ScrabbleGameServer server = new ScrabbleGameServer();
        GameSave gs = test_GameSaveTest.createDummyObject();


        int r = server.saveNewGame(gs);
        System.out.println("Created new game with id: " + r);

    }
}