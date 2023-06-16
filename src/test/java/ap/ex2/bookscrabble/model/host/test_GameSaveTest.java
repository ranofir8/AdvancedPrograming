package ap.ex2.bookscrabble.model.host;

import ap.ex2.GameScrabbleServer.GameSaveMapper;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class test_GameSaveTest {
    public static GameSave createDummyObject() {
        PlayerSave p1 = new PlayerSave("Ofir", 100, "HELLO");
        PlayerSave p2 = new PlayerSave("Gilad", 90, "ORANGE");

        List<PlayerSave> players = new ArrayList<>();
        players.add(p1);
        players.add(p2);

        return new GameSave(123, "hostGilad", players);
    }

}