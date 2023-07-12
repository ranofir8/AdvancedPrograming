package ap.ex2.BookScrabbleServer.dictionary;

public interface CacheReplacementPolicy{
	void add(String word);
	String remove(); 
}
