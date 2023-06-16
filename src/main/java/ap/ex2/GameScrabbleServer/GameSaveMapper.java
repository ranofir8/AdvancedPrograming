package ap.ex2.GameScrabbleServer;

import ap.ex2.GameScrabbleServer.Saves.GameSave;
import org.hibernate.Session;

import java.util.ArrayList;

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
