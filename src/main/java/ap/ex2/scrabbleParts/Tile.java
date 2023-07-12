package ap.ex2.scrabbleParts;

import java.util.Random;

public class Tile {
	public static class Bag {
		private static final int[] TOTAL_QUANTITIES = {9,2,2,4,12,2,3,2,9,1,1,4,2,6,8,2,1,6,4,6,4,2,2,1,2,1};
//		private static final int[] TOTAL_QUANTITIES = {1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1};
		private final int[] quantities;
		private static final int[] scores = {1,3,3,2,1,4,2,4,1,8,5,1,3,1,1,3,10,1,1,1,1,4,4,8,4,10};
		private final Tile[] tiles;
		private final Random rand;
		
		public Bag() {
			this.quantities = Bag.TOTAL_QUANTITIES.clone();
			this.tiles = new Tile[Bag.scores.length];
			this.rand = new Random();
			for (int i = 0; i < Bag.scores.length; i++) {
				this.tiles[i] = new Tile((char) ('A' + i), Bag.scores[i]);
			}
		}
		
		public int size() {
			int sum = 0;
			for (int q : this.quantities)
				sum += q;
			return sum;
		}
		
		public Tile getRand() {
			if (this.size() == 0)
				return null;
			
			int x = rand.nextInt(this.size());
			int index = 0;
			while (this.quantities[index] <= x) {
				x -= this.quantities[index++];
			}
			this.quantities[index]--;
			return this.tiles[index];
		}
		
		public Tile getTile(char c) {
			int index = c-'A';
			if ('A' <= c && c <= 'Z' && this.quantities[index] > 0) {
				this.quantities[index]--;
				return this.tiles[index];
			}
			return null;
		}

		public Tile getTileNoRemove(char c) {
			int index = c-'A';
			if ('A' <= c && c <= 'Z') {
				return this.tiles[index];
			}
			return null;
		}
		
		public void put(Tile t) {
			int index = t.letter - 'A';
			if (this.quantities[index] < Bag.TOTAL_QUANTITIES[index])
				this.quantities[index]++;
		}
		
		public int[] getQuantities() {
			return this.quantities.clone();
		}

		public Tile[] getTileArray(String s, boolean doRemove) {
			Tile[] ts = new Tile[s.length()];
			int i = 0;
			for (char c: s.toCharArray()) {
				if (doRemove)
					ts[i] = this.getTile(c);
				else
					ts[i] = this.getTileNoRemove(c);
				i++;
			}
			return ts;
		}
	}
	
	public final char letter;
	public final int score;
	
	public Tile(char letter, int score) {
		this.letter = letter;
		this.score = score;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + letter;
		result = prime * result + score;
		return result;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Tile other = (Tile) obj;
		if (letter != other.letter)
			return false;
		return score == other.score;
	}
	
	
}
