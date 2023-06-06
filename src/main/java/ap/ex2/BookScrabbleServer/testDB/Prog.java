package ap.ex2.BookScrabbleServer.testDB;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

public class Prog {
    public static void main(String[] args) {
        SessionFactory sF = new Configuration().configure().buildSessionFactory();
        Session sesh = sF.openSession();
        UserMapper map = new UserMapper(sesh);

        map.addUser(new User("Kermit", 20));
        sesh.flush();
    }
}
