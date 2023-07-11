package ap.ex2.bookscrabble.model.host;

import ap.ex3.GameScrabbleServer.Saves.GameSave;
import ap.ex3.GameScrabbleServer.Saves.test_GameSave;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URISyntaxException;

import static org.junit.jupiter.api.Assertions.*;

class HttpClientManagerTest {

    @Test
    // call this test only after the dummyGame is saved in DB server
    void httpGet() throws IOException, URISyntaxException, InterruptedException {
        GameSave game = test_GameSave.createDummyObject();
        GameSave savedGame = HttpClientManager.httpGet("https://localhost:8080/ScrabbleBasicB_war_exploded/scrabble",123,"hostGilad"); //TODO: read url from file
        Assertions.assertTrue(savedGame.equals(game));
    }

    @Test
    void httpPost() throws IOException, URISyntaxException, InterruptedException {
        GameSave game = test_GameSave.createDummyObject();
        int id = HttpClientManager.httpPost("https://localhost:8080/ScrabbleBasicB_war_exploded/scrabble",game);
        Assertions.assertTrue(id == 123);
    }




}