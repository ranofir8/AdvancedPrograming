package ap.ex2.dictionary;

public interface CacheReplacementPolicy{
	void add(String word);
	String remove(); 
}
