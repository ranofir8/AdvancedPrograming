package ap.ex2.mvvm;

import java.io.*;
import java.util.HashMap;

import static java.lang.System.exit;

public class Config {
    static final String configsFilename = "configs.txt";
    private static Config singleton = null;
    private final HashMap<String, String> configs;

    public static final String BOOK_SCRABBLE_IP_KEY = "book_scrabble_ip";
    public static final String BOOK_SCRABBLE_PORT_KEY = "book_scrabble_port";
    public static final String HOST_PORT_KEY = "host_port";
    public static final String DEFAULT_GUEST_IP_KEY = "default_guest_ip";
    public static final String DEFAULT_GUEST_PORT_KEY = "default_guest_port";

    private static final String[] myKeys = {BOOK_SCRABBLE_IP_KEY, BOOK_SCRABBLE_PORT_KEY, HOST_PORT_KEY, DEFAULT_GUEST_IP_KEY, DEFAULT_GUEST_PORT_KEY};

    private Config() {
        this.configs = new HashMap<>();

        try {
            BufferedReader bf = new BufferedReader(new FileReader(Config.configsFilename));

            bf.lines().map(l -> l.split(",")).filter(stArr -> stArr.length == 2).forEach(arr -> configs.put(arr[0], arr[1]));

            bf.close();
        } catch (IOException e) {
            System.out.println("no config.txt file was found. An empty one is created. edit it to your liking.");
            createConfigFile();
            throw new RuntimeException(e);
        }
    }

    private void createConfigFile() {
        File configNew = new File(Config.configsFilename);
        try {
            if (configNew.createNewFile()) {
                PrintWriter pw = new PrintWriter(configNew);
                for (String key : Config.myKeys)
                    pw.println(key + "," + "replace-me");
                pw.close();
                System.out.println("Created config file. edit it and open the game again.");
                exit(0);
            } else {
                System.out.println("config file already exists.");
            }
        } catch (IOException e) {
            System.err.println("can't create config file");
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
