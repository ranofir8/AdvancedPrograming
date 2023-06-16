package ap.ex2.GameScrabbleServer;

import ap.ex2.bookscrabble.model.host.GameSave;
import ap.ex2.bookscrabble.model.host.PlayerSave;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

import java.util.ArrayList;
import java.util.List;

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


//            GameSave gs2 = map.getGameSave(1);
//            PlayerSave emp = (PlayerSave) sesh.get(PlayerSave.class, new Long(2));


        this.sesh.flush();
        return gs.getGameID();
    }

    // when a save HTTP request is coming, this method will be called
    @Override
    public GameSave loadExistingGame(int gameID, String hostName) throws GameNotFoundException, InvalidHostException {
        // todo
        return null;
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
