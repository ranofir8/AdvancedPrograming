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

            User k = new User();
            k.setAge(12);
            k.setEmail("eli@gmail.com");
            k.setFirstName("Kermit");
            k.setLastName("de frog");

            map.saveUser(k);
            sesh.flush();
            sesh.close();
            sF.close();
        } catch (org.hibernate.service.spi.ServiceException  e) {
            System.out.println("SQL server is not running.");
            e.printStackTrace();
        }

    }
}
