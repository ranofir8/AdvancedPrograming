package ap.ex2.GameScrabbleServer.Saves;

import ap.ex2.scrabble.Board;

import java.util.ArrayList;
import java.util.List;

public class test_GameSave {
    public static GameSave createDummyObject() {
        PlayerSave p1 = new PlayerSave("Ofir", 100, "HELLO");
        PlayerSave p2 = new PlayerSave("Gilad", 90, "ORANGE");

        List<PlayerSave> players = new ArrayList<>();
        players.add(p1);
        players.add(p2);

        return new GameSave(123, "hostGilad", new Board(), players);
    }

}