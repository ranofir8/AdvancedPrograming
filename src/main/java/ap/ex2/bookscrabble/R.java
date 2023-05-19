package ap.ex2.bookscrabble;

import java.net.URL;

public class R {
    public static URL getResource(String resourceName) {
        return R.class.getResource(resourceName);
    }
}
