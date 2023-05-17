package ap.ex2.dictionary;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;

public class IOSearcher implements FileSearcher {
	private volatile boolean stop;

	public IOSearcher() {
		this.stop = false;
	}

	@Override
	public boolean search(String word, String... fileNames) {
		for (String fileName : fileNames) {
			FileReader fr = null;
			BufferedReader br = null;
			try {
				fr = new FileReader(fileName);
				br = new BufferedReader(fr);
				
				String line = null;
				while ((line = br.readLine()) != null && !this.stop) {
					// from: https://stackoverflow.com/questions/1128723
					if (Arrays.stream(line.trim().split(" ")).anyMatch(word::equals))
						return true; // the word was found
					
				}
			} catch (IOException e) {
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
