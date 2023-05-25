package ap.ex2.bookscrabble.model;

import ap.ex2.bookscrabble.common.ChangeBooleanProperty;
import ap.ex2.bookscrabble.view.SoundManager;
import ap.ex2.scrabble.Board;
import ap.ex2.scrabble.Tile;
import ap.ex2.scrabble.Word;
import javafx.beans.property.*;
import javafx.beans.value.ObservableValue;

import javax.crypto.BadPaddingException;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * This class is used by the model in order to update for each player the data he knows (and present it in his view).
 */
public class PlayerStatus {
    public final String nickName;
    public BooleanProperty isMyTurnProperty;
    private StringProperty turnOfProperty;
    private List<Tile> handTiles;
    private HashMap<Integer, Tile> tilesInLimbo; // tiles that are not in the Board and not in Hand

    public PlayerStatus(String nickName) {
        this.handTiles = new ArrayList<Tile>();
        this.nickName = nickName;
        this.tilesInLimbo = new HashMap<>();

        this.turnOfProperty = new SimpleStringProperty();
        this.isMyTurnProperty = new SimpleBooleanProperty();
        this.turnOfProperty.addListener((observableValue, s0, s1) -> this.isMyTurnProperty.set(s1.equals(this.nickName)));
        this.isMyTurnProperty.addListener((observableValue, b0, b1) -> {
            if (b1)
                SoundManager.singleton.playSound(SoundManager.SOUND_YOUR_TURN);
        });
    }

    public void updateTurnOf(String nickName) {
        this.turnOfProperty.set(nickName);
    }

    // when the player gets a new tile
    public void addTile(Tile t){
        this.handTiles.add(t);
    }

    // if the tiles are good, remove them from hand
    public void removeTilesInLimbo() {
        this.tilesInLimbo.clear();
    }

    // if the tiles are not good, return them to the hand
    public void putBackTilesFromLimbo() {
        // restore them into hand
        this.tilesInLimbo.forEach((integer, tile) -> this.handTiles.add(tile));
        this.tilesInLimbo.clear();
    }

    /**
     *
     * @param tileIndex - index of gui
     * @param row - row he put on board
     * @param col - col he put on board
     */
    public void moveHandToLimbo(int tileIndex, int row, int col) {
        this.tilesInLimbo.put(Board.positionToInt(row, col), this.handTiles.remove(tileIndex));
    }

    // moves tile from Limbo to Hand
    public void moveLimboToHand(int indexFromLimbo) {
        this.handTiles.add(this.tilesInLimbo.remove(indexFromLimbo));
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
            // todo what if word is only one letter?
            //determine if vertical or horizontal in some way
            return null;
        } else if (columnBin.size() == 1) {
            isVertical = true;
        } else if (rowBin.size() == 1) {
            isVertical = false;
        } else {
            // todo - notify player
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


        String s = "_".repeat(wordLen);
        StringBuilder sb = new StringBuilder(s);
        for (Map.Entry<Integer, Character> entry : relaventSet.entrySet()) {
            sb.setCharAt(entry.getKey() - minValue, entry.getValue());
        }

        String wordString = sb.toString();

        Tile[] ts = bg.getTileArray(wordString, false); //false = do not remove the tile
        int minRow = Collections.min(rowBin.keySet());
        int minCol = Collections.min(columnBin.keySet());
        Word newWord = new Word(ts, minRow, minCol, isVertical);

        return newWord;
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
}
