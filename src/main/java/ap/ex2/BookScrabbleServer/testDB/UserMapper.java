package ap.ex2.BookScrabbleServer.testDB;
import org.hibernate.Session;
public class UserMapper {
    private Session sesh;

    public UserMapper(Session sesh) {
        this.sesh = sesh;
    }

    public void addUser(User u) {
        this.sesh.update(u);
    }
}
