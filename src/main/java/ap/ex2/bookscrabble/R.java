package ap.ex2.bookscrabble;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.net.URISyntaxException;
import java.net.URL;

public class R {
    public static URL getResource(String resourceName) {
        return R.class.getResource(resourceName);
    }

    public static FileReader getFileReaderFromResource(String resourceName) throws URISyntaxException, FileNotFoundException {
        URL fullFilename = R.getResource(resourceName); // "books/mobydick.txt"
        File f1 = new File(fullFilename.toURI());

        return new FileReader(f1);
    }
}
