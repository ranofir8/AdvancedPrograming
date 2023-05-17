package ap.ex2.scrabble;

import java.util.ArrayList;
import java.util.List;

public class Player {
    private int score;
    private boolean isMyTurn;
    private List <Tile> tiles;
    private Board gameBoard;

    public Player() {
        this.score = 0;
        this.isMyTurn = false;
        this.tiles = new ArrayList<Tile>();
    }

    public Board getGameBoard() {
        return this.gameBoard;
    }
}
