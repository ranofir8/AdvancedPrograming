package ap.ex2.bookscrabble.model;

import ap.ex2.scrabble.Board;
import ap.ex2.scrabble.Tile;
import ap.ex2.scrabble.Word;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * This class is used by the model in order to update for each player the data he knows (and present it in his view).
 */
public class PlayerStatus {
    public final String nickName;
    public BooleanProperty isMyTurnProperty;
    public StringProperty turnOfProperty;
    private List<Tile> tiles;
    private List<Word.PositionedTile> tilesInLimbo; // tiles that are not in the Board and not in Hand

    public PlayerStatus(String nickName) {
        this.tiles = new ArrayList<Tile>();
        this.nickName = nickName;
        this.tilesInLimbo = new ArrayList<>();

        this.turnOfProperty = new SimpleStringProperty();
        this.isMyTurnProperty = new SimpleBooleanProperty();
        this.turnOfProperty.addListener((observableValue, s0, s1) -> this.isMyTurnProperty.set(s1.equals(this.nickName)));

    }

    public void updateTurnOf(String nickName) {
        this.turnOfProperty.set(nickName);
    }

    // when the player gets a new tile
    public void addTile(Tile t){
        this.tiles.add(t);
    }

    // if the tiles are good, remove them from hand
    public void removeTilesInLimbo(){
        this.tilesInLimbo.clear();
    }

    // if the tiles are not good, return them to the hand
    public void putBackTilesInLimbo() {
        // restore them into hand
        this.tilesInLimbo.forEach(positionedTile -> {
            this.tiles.add(positionedTile.getTile());
        });
        this.tilesInLimbo.clear();
    }

    /**
     *
     * @param tileIndex - index of gui
     * @param row - row he put on board
     * @param col - col he put on board
     */
    public void moveHandToLimbo(int tileIndex, int row, int col) {
        Word.PositionedTile cur = new Word.PositionedTile(row,col,this.tiles.remove(tileIndex));
        this.tilesInLimbo.add(cur);

    }

    // moves tile from Limbo to Hand
    public void moveLimboToHand(int indexFromLimbo) {
        this.tiles.add(this.tilesInLimbo.remove(indexFromLimbo).getTile());
    }

    public Word limboToWord() {
        //todo
        return null;
    }


    public boolean getIsMyTurn() {
        return this.isMyTurnProperty.get();
    }

    public List<Tile> getTilesInHand() {
        return this.tiles; //*these tiles*
    }
}
