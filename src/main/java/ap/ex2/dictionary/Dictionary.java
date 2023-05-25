package ap.ex2.dictionary;
//213630171

import ap.ex2.bookscrabble.R;

import javax.net.ssl.HandshakeCompletedEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Arrays;

public class Dictionary {
	private CacheManager existingWords;
	private CacheManager nonExistingWords;
	private BloomFilter bloom;
	private volatile ParIOSearcher parIO;
	private String[] fileNames;
	
	public Dictionary(String...fileNames) {
		this.existingWords = new CacheManager(400, new LRU());
		this.nonExistingWords = new CacheManager(100, new LFU());
		this.bloom = new BloomFilter(256, "MD5", "SHA1");
		this.fileNames = fileNames;
		
		this.fillBloomFilter();
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
		synchronized (this) {
			this.parIO = new ParIOSearcher();
		}
		return this.updateCache(word, this.parIO.search(word, this.fileNames));
	}
	
	// synchronized in order to get consistent results about this.parIO 
	public synchronized void close() {
		if (this.parIO != null)
			this.parIO.stop();
	}
}
