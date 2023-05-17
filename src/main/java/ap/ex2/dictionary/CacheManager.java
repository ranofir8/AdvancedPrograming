package ap.ex2.dictionary;
//213630171

import java.util.HashSet;

public class CacheManager {
	private final CacheReplacementPolicy crp;
	private final int size;
	
	private HashSet<String> hashSet;
	
	public CacheManager(int size, CacheReplacementPolicy crp) {
		this.size = size;
		this.crp = crp;
		this.hashSet = new HashSet<String>();
	}
	
	public boolean query(String word) {
		return this.hashSet.contains(word);
	}
	
	public void add(String word) {
		if (this.hashSet.size() == this.size) {
			String removed = this.crp.remove();
			this.hashSet.remove(removed);
		}
			
		this.crp.add(word);
		this.hashSet.add(word);
	}
}
