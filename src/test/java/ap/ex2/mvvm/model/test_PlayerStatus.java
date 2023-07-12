package ap.ex2.mvvm.model;

import ap.ex2.scrabbleParts.Tile;
import ap.ex2.scrabbleParts.Word;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class test_PlayerStatus {

    @Test
    void updateTurnOf() {
        // Create an instance of PlayerStatus
        PlayerStatus playerStatus = new PlayerStatus("John");

        // Call the updateTurnOf method
        playerStatus.setTurnOf("Alice");

        // Assert that the turnOfProperty has been updated correctly
        assertEquals("Alice", playerStatus.getTurnOfWho());
    }

    @Test
    void addTile() {
        // Create an instance of PlayerStatus
        PlayerStatus playerStatus = new PlayerStatus("John");

        // Create a new Tile
        Tile tile = new Tile('A', 1);

        // Call the addTile method
        playerStatus.addTile(tile);

        // Assert that the tile has been added to the handTiles list
        assertTrue(playerStatus.getTilesInHand().contains(tile));
    }

    @Test
    void removeTilesInLimbo() {
        // Create an instance of PlayerStatus
        PlayerStatus playerStatus = new PlayerStatus("John");

        // Add some tiles to the tilesInLimbo map
        playerStatus.getTilesInLimbo().put(0, new Tile('A', 1));
        playerStatus.getTilesInLimbo().put(1, new Tile('B', 2));

        // Call the removeTilesInLimbo method
        playerStatus.removeTilesInLimbo();

        // Assert that the tilesInLimbo map is empty
        assertTrue(playerStatus.getTilesInLimbo().isEmpty());
    }

    @Test
    void putBackTilesFromLimbo() {
        // Create an instance of PlayerStatus
        PlayerStatus playerStatus = new PlayerStatus("John");

        // Add some tiles to the tilesInLimbo map
        playerStatus.getTilesInLimbo().put(0, new Tile('A', 1));
        playerStatus.getTilesInLimbo().put(1, new Tile('B', 2));

        // Call the putBackTilesFromLimbo method
        playerStatus.putBackTilesFromLimbo();

        // Assert that the tilesInLimbo map is empty and the tiles are in handTiles list
        assertTrue(playerStatus.getTilesInLimbo().isEmpty());
        assertFalse(playerStatus.getTilesInHand().isEmpty());
    }

    @Test
    void limboToWord() {
        // Create an instance of PlayerStatus
        PlayerStatus playerStatus = new PlayerStatus("John");

        // Add some tiles to the tilesInLimbo map
        playerStatus.getTilesInLimbo().put(0, new Tile('A', 1));
        playerStatus.getTilesInLimbo().put(1, new Tile('B', 2));

        // Create a Tile.Bag instance
        Tile.Bag tileBag = new Tile.Bag();

        // Call the limboToWord method
        Word word = playerStatus.limboToWord(tileBag);

        // Assert that the word is not null
        assertNotNull(word);
    }

    @Test
    void getIsMyTurn() {
        // Create an instance of PlayerStatus
        PlayerStatus playerStatus = new PlayerStatus("John");

        // Set the isMyTurnProperty to true
        playerStatus.isMyTurnProperty.setValue(true);

        // Call the getIsMyTurn method and assert the result is true
        assertTrue(playerStatus.getIsMyTurn());
    }

    @Test
    void getTurnOfWho() {
        // Create an instance of PlayerStatus
        PlayerStatus playerStatus = new PlayerStatus("John");

        // Set the turnOfProperty to "Alice"
        playerStatus.setTurnOf("Alice");

        // Call the getTurnOfWho method and assert the result is "Alice"
        assertEquals("Alice", playerStatus.getTurnOfWho());
    }

    @Test
    void getTilesInHand() {
        // Create an instance of PlayerStatus
        PlayerStatus playerStatus = new PlayerStatus("John");

        // Add some tiles to the handTiles list
        playerStatus.addTile(new Tile('A', 1));
        playerStatus.addTile(new Tile('B', 2));

        // Call the getTilesInHand method and assert the result matches the added tiles
        List<Tile> tilesInHand = playerStatus.getTilesInHand();
        assertEquals(2, tilesInHand.size());
        assertEquals('A', tilesInHand.get(0).letter);
        assertEquals(1, tilesInHand.get(0).score);
        assertEquals('B', tilesInHand.get(1).letter);
        assertEquals(2, tilesInHand.get(1).score);
    }

    @Test
    void getTilesInLimbo() {
        // Create an instance of PlayerStatus
        PlayerStatus playerStatus = new PlayerStatus("John");

        // Add some tiles to the tilesInLimbo map
        playerStatus.getTilesInLimbo().put(0, new Tile('A', 1));
        playerStatus.getTilesInLimbo().put(1, new Tile('B', 2));

        // Call the getTilesInLimbo method and assert the result matches the added tiles
        HashMap<Integer, Tile> tilesInLimbo = playerStatus.getTilesInLimbo();
        assertEquals(2, tilesInLimbo.size());
        assertEquals('A', tilesInLimbo.get(0).letter);
        assertEquals(1, tilesInLimbo.get(0).score);
        assertEquals('B', tilesInLimbo.get(1).letter);
        assertEquals(2, tilesInLimbo.get(1).score);
    }

    @Test
    void setTurnOf() {
        // Create an instance of PlayerStatus
        PlayerStatus playerStatus = new PlayerStatus("John");

        // Call the setTurnOf method
        playerStatus.setTurnOf("Alice");

        // Assert that the turnOfProperty has been updated correctly
        assertEquals("Alice", playerStatus.getTurnOfWho());
    }

    @Test
    void getSumOfTiles() {
        // Create an instance of PlayerStatus
        PlayerStatus playerStatus = new PlayerStatus("John");

        // Add some tiles to the handTiles list
        playerStatus.addTile(new Tile('A', 1));
        playerStatus.addTile(new Tile('B', 2));

        // Call the getSumOfTiles method and assert the result is the sum of tile scores
        assertEquals(3, playerStatus.getSumOfTiles());
    }

    @Test
    void shuffleTiles() {
        // Create an instance of PlayerStatus
        PlayerStatus playerStatus = new PlayerStatus("John");

        // Add some tiles to the handTiles list
        playerStatus.addTile(new Tile('A', 1));
        playerStatus.addTile(new Tile('B', 2));
        // Call the shuffleTiles method
        playerStatus.shuffleTiles();

        // Assert that the handTiles list has been shuffled
        assertNotEquals("A", playerStatus.getTilesInHand().get(0).letter);
        assertNotEquals("B", playerStatus.getTilesInHand().get(1).letter);
    }
}
