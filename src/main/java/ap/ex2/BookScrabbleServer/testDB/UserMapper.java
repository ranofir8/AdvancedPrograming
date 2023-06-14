package ap.ex2.BookScrabbleServer.testDB;
import org.hibernate.Session;
public class UserMapper {
    private Session sesh = null;


    public void addUser(User u) {
        this.sesh.update(u);
    }


    public UserMapper(Session session) {
        if(session == null)
            throw new RuntimeException("Invalid session object.");
        this.sesh = session;
    }
    public void saveUser(User user){
        sesh.save(user);
    }
    public void updateUser(User user){
        sesh.update(user);
    }
    public void deleteUser(User user) {
        sesh.delete(user);
    }
}