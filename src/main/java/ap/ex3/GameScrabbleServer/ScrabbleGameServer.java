package ap.ex3.GameScrabbleServer;

import ap.ex3.GameScrabbleServer.Saves.GameSave;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;


public class ScrabbleGameServer implements GameServer {
    private HTTPServerManager httpServerManager; // the server of the http. we get from it actions
    private static final SessionFactory sF = new Configuration().configure().buildSessionFactory();
    private Session sesh = null;
    private GameSaveMapper map;

    public ScrabbleGameServer() {
        try {
            this.sesh = sF.openSession();
            this.map = new GameSaveMapper(sesh);
        } catch (org.hibernate.service.spi.ServiceException  e) {
            System.out.println("SQL server is not running.");
            e.printStackTrace();
        }
    }

    @Override
    public int saveNewGame(GameSave gs) {
        this.map.saveGame(gs);

        return gs.getGameID();
    }

    // when a save HTTP request is coming, this method will be called
    @Override
    public GameSave loadExistingGame(int gameID, String hostName) throws GameNotFoundException, InvalidHostException {
        GameSave gs = this.map.getGameSave(gameID);
        if (gs == null)
            throw new GameNotFoundException();
        if (gs.getHostName().equals(hostName)) {
            // remove game from tables
            this.map.deleteGame(gs);
            return gs;
        } else {
            throw new InvalidHostException();
        }
    }

    public void finalize() {
        if (this.sesh != null)
            this.sesh.close();
    }

    private final static Object cleanup = new Object() {
        protected void finalize() {
            ScrabbleGameServer.sF.close();
        }
    };
}
