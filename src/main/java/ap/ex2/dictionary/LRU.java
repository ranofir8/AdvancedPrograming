package ap.ex2.dictionary;

import java.util.LinkedList;
import java.util.Queue;
//213630171
public class LRU implements CacheReplacementPolicy {
	
	private Queue<String> q;
	
	public LRU() {
		this.q = new LinkedList<>();
	}
	
	public void add(String word) {
		boolean isAlreadyIn = this.q.remove(word);
		this.q.add(word);
	}
	
	public String remove() {
		if (this.q.isEmpty())
			return null;
		
		return this.q.remove();
	}

}
