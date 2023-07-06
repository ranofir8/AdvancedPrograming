package ap.ex3.GameScrabbleServer;

import ap.ex3.GameScrabbleServer.Saves.GameSave;
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
        this.sesh.flush();
    }
    public void deleteGame(GameSave gameSave) {
        this.sesh.delete(gameSave);
        this.sesh.flush();
    }
}
