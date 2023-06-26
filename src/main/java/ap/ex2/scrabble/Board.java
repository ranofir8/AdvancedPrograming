package ap.ex2.scrabble;

import ap.ex2.scrabble.Word.PositionedTile;
import ap.ex2.scrabble.Word.WordIterator;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;


public class Board {
	public static final int CHECK_ALL_GOOD = 0;
	public static final int CHECK_OUTSIDE_BOARD_LIMITS = -1;
	public static final int CHECK_NOT_ON_STAR = -2;
	public static final int CHECK_NOT_LEANS_ON_EXISTING_TILES = -3;
	public static final int CHECK_NOT_MATCH_BOARD = -4;
	public static final int CHECK_WORD_NOT_LEGAL = -5;

    public static Board createFromString(String boardGotten, Tile.Bag bagSource) {
		Board b = new Board();
		char[] chars = boardGotten.toUpperCase().toCharArray();
		for (int i = 0; i < chars.length; i++) {
			int r = i / Board.COL_NUM;
			int c = i % Board.COL_NUM;

			Tile t = bagSource.getTileNoRemove(chars[i]);
			if (t != null)
				b.setTileAt(r, c, t);
		}
		return b;
    }


    private static class Multiplier {
		public final int multiplyWordBy;
		public final int multiplyLetterBy;

		private Multiplier(int multiplyBy, boolean forWholeWord) {
			this.multiplyLetterBy = forWholeWord ? 1 : multiplyBy;
			this.multiplyWordBy = forWholeWord ? multiplyBy : 1;
		}

		// default "multiplier"
		private Multiplier() {
			this(1, false);
		}
	}

	public static final int COL_NUM = 15;
	public static final int ROW_NUM = 15;

	// multipliers[col][row], \/	->
	private static final Multiplier[][] multipliers = Board.generateMultipliers();
	private static final Multiplier defaultMultiplier = new Multiplier();

	private final Tile[][] mat;    // mat[col][row], \/	->
	private boolean isEmpty;
	private Function<String, Boolean> atDictionaryCheck;
	private List<String> notLegalWords;

	public Board(Function<String, Boolean> atDictionaryCheck) {
		this.mat = new Tile[Board.COL_NUM][Board.ROW_NUM];
		this.isEmpty = true;
		this.atDictionaryCheck = atDictionaryCheck;
		this.notLegalWords = new ArrayList<>();
	}

	public void setDictioaryCheck(Function<String,Boolean> dictFunc) {
		this.atDictionaryCheck = dictFunc;
	}

	public Board() {
		this(x -> true);
	}

	// creates a multiplier triangle for the basic board
	private static Multiplier[][] generateMultipliers() {
		Multiplier[][] m = new Multiplier[(int)(Board.COL_NUM/2)+1][(int)(Board.ROW_NUM/2)+1];

		// double word score
		m[0][0] = m[3][3] = m[4][4] = m[5][5] = m[6][6] = new Multiplier(2, true);

		// double word score
		m[7][7] = m[7][0] = m[0][7] = new Multiplier(3, true);

		// double letter score
		m[4][0] = m[1][1] = m[5][1] = m[7][4] = new Multiplier(2, false);

		// triple letter score
		m[2][2] = m[6][2] = new Multiplier(3, false);

		return m;
	}

	public int getMultiplierAtInt(int row, int col) {
		Multiplier mult = getMultiplierAt(row, col);
		return mult.multiplyLetterBy + mult.multiplyWordBy * 10;
	}

	public Tile[][] getTiles() {
		Tile[][] copyMat = new Tile[Board.COL_NUM][Board.ROW_NUM];
		for (int i = 0; i < copyMat.length; i++) {
			copyMat[i] = this.mat[i].clone();
		}
		return copyMat;
	}

	// returns the tile at the specified position
	public Tile getTileAt(int row, int col) {
		if (0 > row || row >= Board.ROW_NUM)
			return null;
		if (0 > col || col >= Board.COL_NUM)
			return null;
		return this.mat[col][row];
	}

	// sets the given tile at the specified position
	private void setTileAt(int row, int col, Tile t) {
		if (0 > row || row >= Board.ROW_NUM)
			return;
		if (0 > col || col >= Board.COL_NUM)
			return;
		this.mat[col][row] = t;
		this.isEmpty = false;
	}

	public boolean boardLegal(Word w) {
		return this.boardLegalInt(w) == 0;
	}

	/**
	 * @return int status of Word
	 *  if everything is good, returns 0
	 * 	if the word is outside the borders, returns -1
	 *  if the word is not on star (and it should be), returns -2
	 *  if the word is not leaning on existing tiles, returns -3
	 *  if the word does not match the board, returns -4
	 */
	public int boardLegalInt(Word w) {
		// the word is inside the board
		if (w.getCol() < 0 || w.getCol() >= Board.COL_NUM)
			return CHECK_OUTSIDE_BOARD_LIMITS;
		if (w.getRow() < 0 || w.getRow() >= Board.ROW_NUM)
			return CHECK_OUTSIDE_BOARD_LIMITS;
		if (w.isVertical()) {
			if (w.getRow() + w.getTiles().length - 1 >= Board.ROW_NUM)
				return CHECK_OUTSIDE_BOARD_LIMITS;
		} else {
			if (w.getCol() + w.getTiles().length - 1 >= Board.COL_NUM)
				return CHECK_OUTSIDE_BOARD_LIMITS;
		}

		// leans on an existing word
		boolean leansOnExsistingTiles = false;
		boolean onStar = false;
		WordIterator it = w.getSurroundIterator();
		while (it.hasNext()) {
			PositionedTile data = it.next();
			Tile boardTile = this.getTileAt(data.getRow(), data.getCol());
			Tile wordTile = data.getTile();

			// board is empty, check if the word leans on the star tile
			if (this.isEmpty && wordTile != null && this.isStarTile(data.getRow(), data.getCol()))
				onStar = true;
				// board is NOT empty, check if the word leans on another
			else if (boardTile != null) {
				leansOnExsistingTiles = true;
				// if a place has a board tile and a word and they don't match
				if (wordTile != null && !boardTile.equals(wordTile))
					return CHECK_NOT_MATCH_BOARD;
			}
		}

		if (this.isEmpty && !onStar)
			return CHECK_NOT_ON_STAR;
		else if (!this.isEmpty && !leansOnExsistingTiles)
			return CHECK_NOT_LEANS_ON_EXISTING_TILES;
		else
			return CHECK_ALL_GOOD;
	}

	// returns True is the coordinates are of the star tile
	private boolean isStarTile(int y, int x) {
		return x==7 && y==7;
	}

	private boolean dictionaryLegal(String word) {
		return this.atDictionaryCheck.apply(word);
	}

	// gets a word and returns all of the new words created on the board if that word will be placed
	// returns only words of length at least 2 ; if w's length is less than 2, returns an empty list.
	// the first word will always be the full current word
	private ArrayList<Word> getWords(Word w) {
		ArrayList<Word> l = new ArrayList<Word>();

		// put FULL current word, if it is one letter, stop and return empty list
		Word fullWord = this.getFullWord(w);
		if (fullWord.getTiles().length >= 2)
			l.add(fullWord);

		// go over letters in word and put the new words
		WordIterator it = w.getInnerWordIterator();
		while (it.hasNext()) {
			PositionedTile data = it.next();

			Tile wordTile = data.getTile();
			Tile boardTile = this.getTileAt(data.getRow(), data.getCol());
			if (boardTile == null) {  // this tile is new, check words that come from it
				// check if the rotated word at Tile exists
				// go to start of the word that lays on the current location
				Word newWord = this.getStartOfWordAt(data.getRow(), data.getCol(), !w.isVertical(), new Tile[]{wordTile});
				// allowing only words with at least two letters
				if (newWord.getTiles().length > 1)
					l.add(newWord);
			}
		}

		return l;
	}

	// gets a row, col that includes the word, a searching direction, and a "center" tiles
	// center tiles will contain one tile for regular calls, and a list for FULL word searches
	private Word getStartOfWordAt(int searchRow, int searchCol, boolean isVertical, Tile[] centerTiles) {
		int deltaCol = isVertical ? 0 : 1;
		int deltaRow = 1 - deltaCol;

		ArrayList<Tile> wordTiles = new ArrayList<Tile>();

		// previous tiles
		int currentRow = searchRow, currentCol = searchCol;
		while (this.getTileAt(currentRow - deltaRow, currentCol - deltaCol) != null) {
			currentRow -= deltaRow;
			currentCol -= deltaCol;

			wordTiles.add(0, this.getTileAt(currentRow, currentCol));
		}
		int startCol = currentCol, startRow = currentRow;

		// current tiles, reset current indices and append centerTiles
		currentRow = searchRow - deltaRow;
		currentCol = searchCol - deltaCol;
		for (Tile t : centerTiles) {
			wordTiles.add(t);
			currentRow += deltaRow;
			currentCol += deltaCol;
		}

		// next tiles
		while (this.getTileAt(currentRow + deltaRow, currentCol + deltaCol) != null) {
			currentRow += deltaRow;
			currentCol += deltaCol;

			wordTiles.add(this.getTileAt(currentRow, currentCol));
		}

		return new Word(wordTiles.toArray(new Tile[0]), startRow, startCol, isVertical);
	}

	// imagines all of w in the board
	private Word getFullWord(Word w) {
		return this.getStartOfWordAt(w.getRow(), w.getCol(), w.isVertical(), w.getTiles());
	}

	// gets a word and returns its score
	private int getScore(Word w) {
		int[] score = {0};
		int[] wordMult = {1};

		// go over letters in word and calculate their score
		((Iterator<PositionedTile>) w.getInnerWordIterator()).forEachRemaining(data -> {
			// get the multiplier of the current tile, if this tile is new
			Tile boardTile = getTileAt(data.getRow(), data.getCol());
			Multiplier mul = Board.getMultiplierAt(data.getRow(), data.getCol());
			if (isStarTile(data.getRow(), data.getCol()) && boardTile != null)
				mul = Board.defaultMultiplier;

			score[0] += data.getTile().score * mul.multiplyLetterBy;
			wordMult[0] *= mul.multiplyWordBy;
		});

		return score[0] * wordMult[0];
	}

	// gets the multiplier of a tile
	private static Multiplier getMultiplierAt(int row, int col) {
		int effectiveRow = Math.abs(row - (int)(Board.ROW_NUM / 2));
		int effectiveCol = Math.abs(col - (int)(Board.COL_NUM / 2));

		if (effectiveRow > effectiveCol) {
			int tmp = effectiveRow;
			effectiveRow = effectiveCol;
			effectiveCol = tmp;
		}

		Multiplier mul = Board.multipliers[effectiveCol][effectiveRow];
		if (mul == null)
			mul = Board.defaultMultiplier;
		return mul;
	}

	// gets a word with missing tiles, and fills them up will existing tiles
	private Word fillEmptyWordPlaces(Word w) {
		ArrayList<Tile> newTiles = new ArrayList<Tile>();
		WordIterator it = w.getInnerWordIterator();
		boolean stop = false;
		while (it.hasNext() && !stop) {
			PositionedTile data = it.next();
			Tile wordTile = data.getTile();
			Tile boardTile = this.getTileAt(data.getRow(), data.getCol());
			if (wordTile != null) {
				newTiles.add(wordTile);
			} else if (boardTile != null) {
				newTiles.add(boardTile);
			} else {
				// the word on the board will not create one full word. stop here
				stop = true;
			}
		}
		return new Word((newTiles.toArray(new Tile[0])), w.getRow(), w.getCol(), w.isVertical());
	}

	// places a word on the board; return the score of that move
	// if a negative score is returned, an error occurred
	public int tryPlaceWord(Word word) {
		// all of the previous methods assume filled words!
		Word filledWord = this.fillEmptyWordPlaces(word);
		// if the word is not board legal, return 0
		int tryPlaceErr = this.boardLegalInt(filledWord);
		if (tryPlaceErr != 0) {
			return tryPlaceErr;
		}
		// the first word is the FULL w, check if it (and all the other words) are legal
		ArrayList<Word> newWords = this.getWords(filledWord);
		this.notLegalWords = newWords.stream().map(w -> w.toString()).filter(w -> !dictionaryLegal(w)).collect(Collectors.toList());
		if (notLegalWords.size() > 0)
			return CHECK_WORD_NOT_LEGAL;
		// calculate scores
		int[] score = {0};
		newWords.forEach(newWord -> score[0] += getScore(newWord));

		// place word tiles on the board
		// go over tiles in FULL word and place them
		((Iterator<PositionedTile>)filledWord.getInnerWordIterator()).forEachRemaining(data -> setTileAt(data.getRow(), data.getCol(), data.getTile()));

		return score[0];
	}

	public void printBoard() {
		for (int row = 0; row < Board.ROW_NUM; row++) {
			for (int col = 0; col < Board.COL_NUM; col++) {
				Tile t = this.getTileAt(row, col);
				if (t == null)
					System.out.print("_");
				else
					System.out.print(t.letter);
			}
			System.out.println();
		}
		System.out.println("\n");
	}

	/** @return int value of a point on the board **/
	public static int positionToInt(int row, int col) {
		return row * Board.ROW_NUM + col;
	}

	public static PositionedTile intToPositionedTile(int index, Tile t) {
		int r = index / Board.ROW_NUM, c = index % Board.ROW_NUM;
		return new PositionedTile(r, c, t);
	}

	public List<String> getNotLegalWords() {
		return this.notLegalWords;
	}

	public String summeryString() {
		StringBuilder sb = new StringBuilder();
		for (int row = 0; row < Board.ROW_NUM; row++) {
			for (int col = 0; col < Board.COL_NUM; col++) {
				Tile t = this.getTileAt(row, col);
				if (t == null)
					sb.append(' ');
				else
					sb.append(t.letter);
			}
		}
		return sb.toString();
	}
}

