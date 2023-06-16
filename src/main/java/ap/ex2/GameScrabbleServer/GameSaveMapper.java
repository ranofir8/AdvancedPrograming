package ap.ex2.GameScrabbleServer;

import ap.ex2.bookscrabble.model.host.GameSave;
import org.hibernate.Session;

public class GameSaveMapper {
    private final Session sesh;

    public GameSaveMapper(Session session) {
        if(session == null)
            throw new RuntimeException("Invalid session object.");
        this.sesh = session;
    }

    public GameSave getGameSave(int gameID) {
        GameSave emp = (GameSave) this.sesh.get(GameSave.class, gameID);
        return emp;
    }



    public void saveGame(GameSave gameSave){
        this.sesh.save(gameSave);
    }
    public void deleteGame(int gameID) {
//        Query q = this.sesh.createQuery("delete from game_save where id = :gameID");
//        q.executeUpdate();
        // todo
    }
}
