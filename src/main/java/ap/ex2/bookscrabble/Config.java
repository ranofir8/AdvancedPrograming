package ap.ex2.bookscrabble;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;

public class Config {
    static final String configsFilename = "configs.txt";
    private static Config singleton = null;
    private final HashMap<String, String> configs;

    public static final String BOOK_SCRABBLE_IP_KEY = "book_scrabble_ip";
    public static final String BOOK_SCRABBLE_PORT_KEY = "book_scrabble_port";
    public static final String HOST_PORT_KEY = "host_port";
    public static final String DEFAULT_GUEST_IP_KEY = "default_guest_ip";
    public static final String DEFAULT_GUEST_PORT_KEY = "default_guest_port";

    private Config() {
        this.configs = new HashMap<String, String>();

        try {
            BufferedReader bf = new BufferedReader(new FileReader(Config.configsFilename));

            bf.lines().map(l -> l.split(",")).filter(stArr -> stArr.length == 2).forEach(arr -> configs.put(arr[0], arr[1]));

            bf.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static Config getInstance() {
        if (Config.singleton == null)
            Config.singleton = new Config();
        return Config.singleton;
    }

    public String get(String key) {
        return this.configs.getOrDefault(key, null);
    }
}
