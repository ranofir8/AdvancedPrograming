package ap.ex2.BookScrabbleServer.dictionary;
//213630171

import ap.ex2.mvvm.R;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Arrays;

public class Dictionary {
	private final CacheManager existingWords;
	private final CacheManager nonExistingWords;
	private final BloomFilter bloom;
	private volatile ParIOSearcher parIO;
	private final String[] fileNames;

	public Dictionary(String...fileNames) {
		this.existingWords = new CacheManager(400, new LRU());
		this.nonExistingWords = new CacheManager(100, new LFU());
		this.bloom = new BloomFilter((int) Math.pow(2, 28), "MD5", "SHA1"); // 270,000 = 2^18
		this.fileNames = fileNames;

		System.out.print("Indexing book(s) " + String.join(", ", fileNames) + "... \t");
		this.fillBloomFilter();
		System.out.println("done.");
	}

	private void fillBloomFilter() {
		for (String fileName : fileNames) {
			FileReader fr = null;
			BufferedReader br = null;
			try {
				fr = R.getFileReaderFromResource("books/" + fileName);
				br = new BufferedReader(fr);

				String line = null;
				while ((line = br.readLine()) != null) {
					// from: https://stackoverflow.com/questions/1128723
					Arrays.stream(line.trim().split(" ")).forEach(bloom::add);
				}
			} catch (IOException e) {
				e.printStackTrace();
			} catch (URISyntaxException e) {
				throw new RuntimeException(e);
			} finally {
				if (fr != null)
					try {
						if (br != null)
							br.close();
						if (fr != null)
							fr.close();
					} catch (IOException e) {}
			}
		}
	}

	public boolean query(String word) {
		word = word.toLowerCase();

		if (this.existingWords.query(word))
			return true;
		if (this.nonExistingWords.query(word))
			return false;

		return this.updateCache(word, this.bloom.contains(word));
	}

	// updates the relevant cache regarding Word
	private boolean updateCache(String word, boolean answer) {
		CacheManager cm = answer ? this.existingWords : this.nonExistingWords;
		cm.add(word);
		return answer;
	}

	public boolean challenge(String word) {
		word = word.toLowerCase();

		synchronized (this) {
			this.parIO = new ParIOSearcher();
		}
		boolean b = this.parIO.search(word, this.fileNames);
		return this.updateCache(word, b);
	}

	// synchronized in order to get consistent results about this.parIO
	public synchronized void close() {
		if (this.parIO != null)
			this.parIO.stop();
	}
}
