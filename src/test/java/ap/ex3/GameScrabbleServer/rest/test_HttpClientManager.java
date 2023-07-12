package ap.ex3.GameScrabbleServer.rest;

import ap.ex3.GameScrabbleServer.Saves.GameSave;
import ap.ex3.GameScrabbleServer.Saves.test_GameSave;
import ap.ex3.GameScrabbleServer.db.DBServerException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URISyntaxException;

import static org.junit.jupiter.api.Assertions.*;

class test_HttpClientManager {

    final static String BASE_URL = "http://localhost:18020/ScrabbleBasicB_war_exploded/scrabble";

    @Test
        // call this test only after the dummyGame is saved in DB server
    void httpGet() {
        GameSave game = test_GameSave.createDummyObject();
        HttpClientManager hcm = null;
        try {
            hcm = new HttpClientManager(BASE_URL);
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }

        GameSave savedGame = null;
        try {
            savedGame = hcm.httpGet(9479,"hostGilad");
        } catch (IOException e) {
            fail("Can't connect to HTTP server");
        } catch (DBServerException e) {
            fail("HTTP server encountered an error");
        } catch (GameServer400 e) {
            fail("HTTP 400 message: " + e.getMessage());
        }

    }

    @Test
    void httpPost() {
        GameSave game = test_GameSave.createDummyObject();
        HttpClientManager hcm = null;
        try {
            hcm = new HttpClientManager(BASE_URL);
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
        int id = 0;
        try {
            id = hcm.httpPost(game);
        } catch (IOException e) {
            fail("Can't connect to HTTP server");
        } catch (DBServerException e) {
            fail("HTTP server encountered an error");
        } catch (GameServer400 e) {
            fail("HTTP 400 message: " + e.getMessage());
        }
        System.out.println("Created a new game with ID: " + id);
    }

}