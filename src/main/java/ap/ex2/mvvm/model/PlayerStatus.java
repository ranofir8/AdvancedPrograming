package ap.ex2.mvvm.model;

import ap.ex2.mvvm.common.ChangeBooleanProperty;
import ap.ex2.scrabbleParts.Board;
import ap.ex2.scrabbleParts.Tile;
import ap.ex2.scrabbleParts.Word;
import javafx.beans.property.*;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * This class is used by the model in order to update for each player the data he knows (and present it in his view).
 */
public class PlayerStatus {
    private final ChangeBooleanProperty myTilesChangeEvent;
    public BooleanProperty isMyTurnProperty;
    private final StringProperty turnOfProperty;

    public final String nickName;
    private final List<Tile> handTiles;
    private final HashMap<Integer, Tile> tilesInLimbo; // tiles that are not in the Board and not in Hand

    public PlayerStatus(String nickName) {
        this.handTiles = new ArrayList<>();
        this.nickName = nickName;
        this.tilesInLimbo = new HashMap<>();

        this.turnOfProperty = new SimpleStringProperty();
        this.isMyTurnProperty = new SimpleBooleanProperty();
        this.turnOfProperty.addListener((observableValue, s0, s1) -> this.isMyTurnProperty.set(s1.equals(this.nickName)));
        this.myTilesChangeEvent = new ChangeBooleanProperty();
    }




    // when the player gets a new tile
    public void addTile(Tile t){
        this.handTiles.add(t);
        this.myTilesChangeEvent.alertChanged();
    }

    // if the tiles are good, remove them from hand
    public void removeTilesInLimbo() {
        this.tilesInLimbo.clear();
        this.myTilesChangeEvent.alertChanged();
    }

    // if the tiles are not good, return them to the hand
    public void putBackTilesFromLimbo() {
        // restore them into hand
        this.tilesInLimbo.forEach((integer, tile) -> this.handTiles.add(tile));
        // skip SOUND of got tiles b.c a new tile will be added shortly (skipped turn)
        this.tilesInLimbo.clear();
        this.myTilesChangeEvent.alertChanged();
    }

    /**
     *
     * @param tileIndex - index of gui
     * @param row - row he put on board
     * @param col - col he put on board
     */
    public void moveHandToLimbo(int tileIndex, int row, int col) {
        this.tilesInLimbo.put(Board.positionToInt(row, col), this.handTiles.remove(tileIndex));
        this.myTilesChangeEvent.alertChanged();
    }

    // moves tile from Limbo to Hand
    public void moveLimboToHand(int indexFromLimbo) {
        this.handTiles.add(this.tilesInLimbo.remove(indexFromLimbo));
        this.myTilesChangeEvent.alertChanged();
    }

    public Word limboToWord(Tile.Bag bg) {
        HashMap<Integer, Character> columnBin = new HashMap<>();
        HashMap<Integer, Character> rowBin = new HashMap<>();
        boolean isVertical = false;

        List<Word.PositionedTile> l = this.tilesInLimbo.entrySet().stream()
                .map(entry -> Board.intToPositionedTile(entry.getKey(), entry.getValue()))
                .collect(Collectors.toList());

        for (Word.PositionedTile pt : l) {
            columnBin.put(pt.getCol(), pt.getTile().letter);
            rowBin.put(pt.getRow(), pt.getTile().letter);
        }

        HashMap<Integer, Character> relaventSet = null;
        HashMap<Integer, Character> otherSet = null;

        // check if all tiles form a straight line
        if(columnBin.keySet().size() == 1 && rowBin.keySet().size() == 1) { //only one tile was added
            isVertical = true;
        } else if (columnBin.size() == 1) {
            isVertical = true;
        } else if (rowBin.size() == 1) {
            isVertical = false;
        } else {
            //illegal!
            return null;
        }

        if (isVertical) {
            relaventSet = rowBin;
            otherSet = columnBin;
        } else {
            relaventSet = columnBin;
            otherSet = rowBin;
        }

        // if the following lines are performed, then what the player put is legal
        int minValue = Collections.min(relaventSet.keySet());
        int maxValue = Collections.max(relaventSet.keySet());


        int wordLen = maxValue - minValue + 1;

        String s = IntStream.range(0, wordLen).mapToObj(x->"_").collect(Collectors.joining());
        StringBuilder sb = new StringBuilder(s);
        for (Map.Entry<Integer, Character> entry : relaventSet.entrySet()) {
            sb.setCharAt(entry.getKey() - minValue, entry.getValue());
        }

        String wordString = sb.toString();

        Tile[] ts = bg.getTileArray(wordString, false); //false = do not remove the tile
        int minRow = Collections.min(rowBin.keySet());
        int minCol = Collections.min(columnBin.keySet());
        return new Word(ts, minRow, minCol, isVertical);
    }


    public boolean getIsMyTurn() {
        return this.isMyTurnProperty.get();
    }

    public String getTurnOfWho() {
        return this.turnOfProperty.get();
    }

    public List<Tile> getTilesInHand() {
        return this.handTiles; //*these tiles*
    }

    public HashMap<Integer, Tile> getTilesInLimbo() {
        return this.tilesInLimbo;
    }

    public void setTurnOf(String turnOfNickname) {
        this.turnOfProperty.set(turnOfNickname);
    }

    public void bindToPlayerTurn(ChangeBooleanProperty cbp) {
        cbp.changeByProperty(this.turnOfProperty);
    }

    public void bindToTilesChanged(ChangeBooleanProperty cbp) {
        cbp.changeByProperty(this.myTilesChangeEvent);
    }

    public int getSumOfTiles() {
        return this.handTiles.stream().mapToInt(tile ->tile.score).sum();
    }

    public void shuffleTiles() {
        Collections.shuffle(this.handTiles);
        this.myTilesChangeEvent.alertChanged();
    }
}
