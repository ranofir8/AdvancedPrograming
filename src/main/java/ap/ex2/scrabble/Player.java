package ap.ex2.scrabble;

import java.util.List;

public class Player {
    private int score;
    private boolean isMyTurn;
    private List <Tile> tiles;

    public Player(int score, boolean isMyTurn, List<Tile> tiles) {
        this.score = score;
        this.isMyTurn = isMyTurn;
        this.tiles = tiles;
    }
}
