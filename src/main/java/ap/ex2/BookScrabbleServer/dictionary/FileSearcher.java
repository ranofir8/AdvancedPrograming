package ap.ex2.BookScrabbleServer.dictionary;

public interface FileSearcher {
	boolean search(String word, String...fileNames);
	void stop();
}
