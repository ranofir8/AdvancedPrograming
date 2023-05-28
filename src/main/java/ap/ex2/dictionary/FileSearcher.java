package ap.ex2.dictionary;

public interface FileSearcher {
	boolean search(String word, String...fileNames);
	void stop();
}
