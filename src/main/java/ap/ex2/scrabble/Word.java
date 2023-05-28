package ap.ex2.scrabble;

import java.util.Arrays;
import java.util.Iterator;

public class Word {
	private final Tile[] tiles;
	private final int row, col;
	private final boolean vertical;

	public Word(Tile[] tiles, int row, int col, boolean vertical) {
		this.tiles = tiles;
		this.row = row;
		this.col = col;
		this.vertical = vertical;
	}

	public String toString() {
		String tmp = "";
		for (Tile t : this.tiles)
			tmp += (t == null) ? '_' : t.letter;
		return tmp;
	}

	public int tileAmount() {
		return (int) Arrays.stream(this.tiles).filter(t -> t != null).count();

	}

	public Tile[] getTiles() {
		return tiles;
	}

	public int getRow() {
		return row;
	}

	public int getCol() {
		return col;
	}

	public boolean isVertical() {
		return vertical;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Word other = (Word) obj;
		if (col != other.col)
			return false;
		if (row != other.row)
			return false;
		if (!Arrays.equals(tiles, other.tiles))
			return false;
		return vertical == other.vertical;
	}

	public String toNetworkString() {
		// example of pattern: "2,4,FARM,false"
		return this.row + "," + this.col + "," + this + "," + this.vertical;
	}

	public static Word getWordFromNetworkString(String networkString, Tile.Bag bag) {
		// construct a Word object using a String "2,4,FARM,false"
		// bag to get tiles from
		String[] parts = networkString.split(",");
		if (parts.length != 4) {
			return null;
		}
		int row = Integer.parseInt(parts[0]);
		int col = Integer.parseInt(parts[1]);
		Tile[] t = bag.getTileArray(parts[2],false);
		boolean b = Boolean.parseBoolean(parts[3]);

		return new Word(t, row, col, b);
		// Tile[] tiles, int row, int col, boolean vertical
	}

	public static class PositionedTile {
		private final int tRow;
		private final int tCol;
		private final Tile tile;

		public PositionedTile(int row, int col, Tile tile) {
			this.tRow = row;
			this.tCol = col;
			this.tile = tile;
		}

		public int getRow() {
			return this.tRow;
		}

		public int getCol() {
			return this.tCol;
		}

		public Tile getTile() {
			return this.tile;
		}
	}

	public WordIterator getSurroundIterator() {
		return new SurroundIterator(this);
	}

	public WordIterator getInnerWordIterator() {
		return new InnerWordIterator(this);
	}

	public static abstract class WordIterator implements Iterator {
		private final Word word;
		protected int offsetX, offsetY;
		protected int height, width;
		protected boolean isDone;

		private WordIterator(Word w, int offsetX, int offsetY) {
			this.word = w;
			this.width = this.word.isVertical() ? 1 : this.word.tiles.length;
			this.height = this.word.isVertical() ? this.word.tiles.length : 1;
			this.isDone = false;
			this.offsetX = offsetX;
			this.offsetY = offsetY;
		}

		@Override
		public boolean hasNext() {
			return !this.isDone;
		}

		@Override
		public PositionedTile next() {
			if (this.isDone)
				return null;

			// normal tile
			Tile tile = null;
			int index = this.word.isVertical() ? this.offsetY : this.offsetX;
			int otherIndex = this.word.isVertical() ? this.offsetX : this.offsetY;

			if (0 <= index && index < this.word.getTiles().length && 0 == otherIndex)
				tile = this.word.tiles[index];

			PositionedTile t = new PositionedTile(this.word.getRow() + this.offsetY, this.word.getCol() + this.offsetX, tile);
			// go to next place
			this.updateOffsets();
			return t;
		}

		protected abstract void updateOffsets();
	}

	// iterates over the surrounding and inner tiles of the word
	private static class SurroundIterator extends WordIterator {
		private SurroundIterator(Word w) {
			super(w, 0, -1);
		}

		protected void updateOffsets() {
			do {
				// one step
				this.offsetX++;
				if (this.offsetX > this.width) {
					this.offsetY++;
					this.offsetX = -1;
				}

				if (this.offsetY > this.height) {
					this.isDone = true;
				}
			} while (!this.isDone && this.hasReachedCorner());
		}

		// reached corner
		private boolean hasReachedCorner() {
			return (this.offsetX == -1 || this.offsetX == this.width) && (this.offsetY == -1 || this.offsetY == this.height);
		}
	}


	// iterates over the word tiles
	private static class InnerWordIterator extends WordIterator {
		private InnerWordIterator(Word w) {
			super(w, 0, 0);
		}

		protected void updateOffsets() {
			// next tile
			this.offsetX++;
			if (this.offsetX >= this.width) {
				this.offsetY++;
				this.offsetX = 0;
			}

			if (this.offsetY >= this.height) {
				this.isDone = true;
			}
		}
	}
}
