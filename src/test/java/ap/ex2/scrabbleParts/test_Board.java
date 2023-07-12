package ap.ex2.scrabbleParts;

import org.junit.jupiter.api.Test;

class test_Board {

    @Test
    void createFromString() {
        Tile.Bag bag = new Tile.Bag();
        String sb = " hello                           world                  j";
        Board b = Board.createFromString(sb, bag);
        b.printBoard();

    }
}