package ap.ex2.BookScrabbleServer.testDB;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

public class Prog {
    public static void main(String[] args) {
        try {
            SessionFactory sF = new Configuration().configure().buildSessionFactory();
            Session sesh = sF.openSession();
            UserMapper map = new UserMapper(sesh);

            //map.addUser(new User("Kermit", 20));
            sesh.flush();
        } catch (org.hibernate.service.spi.ServiceException  e) {
            System.out.println("SQL server is not running.");
        }

    }
}
