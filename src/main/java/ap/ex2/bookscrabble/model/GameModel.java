package ap.ex2.bookscrabble.model;

import ap.ex2.bookscrabble.common.Command;
import ap.ex2.bookscrabble.common.Command2VM;
import ap.ex2.bookscrabble.common.Protocol;
import ap.ex2.bookscrabble.view.PlayerRowView;
import javafx.beans.property.ObjectProperty;

import java.net.Socket;
import java.util.HashMap;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

public abstract class GameModel extends Model {
    protected GameInstance gi;

    public GameModel(String nickname) {
        this.gi = new GameInstance(nickname);
    }

    public GameInstance getGameInstance() {
        return this.gi;
    }

    public abstract int getDisplayPort();

    public abstract void establishConnection() throws Exception;

    protected abstract void closeConnection();

    public List<PlayerRowView> getPlayerList() {
        return this.gi.getPlayerList();
    }

    protected void onNewPlayer(String newplayerName) {
        this.gi.updateScoreBoard(newplayerName, 0);
    }

    public void onStartGame() {
        this.gi.onStartGame();
    }
}
