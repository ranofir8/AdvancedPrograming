package ap.ex2.scrabble;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class test_Board {

    @Test
    void createFromString() {
        Tile.Bag bag = new Tile.Bag();
        String sb = " hello                           world                  j";
        Board b = Board.createFromString(sb, bag);
        b.printBoard();

    }
}