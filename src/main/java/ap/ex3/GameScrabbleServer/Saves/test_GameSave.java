package ap.ex3.GameScrabbleServer.Saves;

import ap.ex2.scrabble.Board;
import ap.ex2.scrabble.Tile;

import java.util.ArrayList;
import java.util.List;

public class test_GameSave {
    public static GameSave createDummyObject() {
        PlayerSave p1 = new PlayerSave("Ofir", 100, "HELSLKOTILE");
        PlayerSave p2 = new PlayerSave("Gilad", 90, "FORANGE");
        PlayerSave p3 = new PlayerSave("Mor", -80, "CYBERRR");

        List<PlayerSave> players = new ArrayList<>();

        players.add(p2);
        players.add(p3);
        players.add(p1);
        Tile.Bag b = new Tile.Bag();

        return new GameSave(123, "hostGilad", Board.createFromString("ALOF     SADE", b), players);
    }

}