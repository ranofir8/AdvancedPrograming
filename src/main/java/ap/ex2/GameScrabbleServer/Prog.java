package ap.ex2.GameScrabbleServer;
import ap.ex2.GameScrabbleServer.GameSaveMapper;
import ap.ex2.bookscrabble.model.host.GameSave;
import ap.ex2.bookscrabble.model.host.PlayerSave;
import ap.ex2.scrabble.Board;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

import java.util.ArrayList;
import java.util.List;

public class Prog {
    public static void main(String[] args) {
        try {
            SessionFactory sF = new Configuration().configure().buildSessionFactory();
            Session sesh = sF.openSession();

            GameSaveMapper map = new GameSaveMapper(sesh);


            PlayerSave p1 = new PlayerSave("Ofir", 100, "HELLO");
            PlayerSave p2 = new PlayerSave("Gilad", 90, "ORANGE");

            List<PlayerSave> players = new ArrayList<>();
            players.add(p1);
            players.add(p2);

            GameSave gs = new GameSave(123, "hostGilad", players);

            map.saveGame(gs);


//            GameSave gs = map.getGameSave(1);
//            PlayerSave emp = (PlayerSave) sesh.get(PlayerSave.class, new Long(2));


            sesh.flush();
            sesh.close();
            sF.close();
        } catch (org.hibernate.service.spi.ServiceException  e) {
            System.out.println("SQL server is not running.");
            e.printStackTrace();
        }

    }
}
