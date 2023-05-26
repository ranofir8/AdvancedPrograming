package ap.ex2.dictionary;

import ap.ex2.bookscrabble.R;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Arrays;

public class IOSearcher implements FileSearcher {
	private volatile boolean stop;

	public IOSearcher() {
		this.stop = false;
	}

	@Override
	public boolean search(String word, String... fileNames) {
		word = word.toLowerCase();

		for (String fileName : fileNames) {
			FileReader fr = null;
			BufferedReader br = null;
			try {
				fr = R.getFileReaderFromResource("books/" + fileName);
				br = new BufferedReader(fr);
				
				String line = null;
				while ((line = br.readLine()) != null && !this.stop) {
					// from: https://stackoverflow.com/questions/1128723
					if (Arrays.stream(line.trim().split(" ")).map(String::toLowerCase).anyMatch(word::equals))
						return true; // the word was found
					
				}
			} catch (IOException | URISyntaxException e) {
				e.printStackTrace();
				return false; // an exception occurred
			} finally {
				if (fr != null)
					try {
						if (br != null)
							br.close();
						if (fr != null)
							fr.close();
					} catch (IOException e) {
					}
			}
			
			if (this.stop)
				return false;
			
		}
		return false;
	}

	@Override
	public void stop() {
		this.stop = true;
	}
	
}
