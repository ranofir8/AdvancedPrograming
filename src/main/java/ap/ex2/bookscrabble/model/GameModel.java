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
    public static final int MIN_PLAYERS = 2;
    public static final int MAX_PLAYERS = 4;
    public static final int DRAW_START_AMOUNT = 7;
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

    protected void onRecvMessage(String sentBy, String msgContent) {
        // nickname will always be null, because only the host can send message to guests
        char msgProtocol = msgContent.charAt(0); //when a message is sent, the first part is a protocol
        String msgExtra = msgContent.substring(1, msgContent.length()); //rest of the message contain details

        this.handleProtocolMsg(sentBy, msgProtocol, msgExtra);
    }

    protected boolean handleProtocolMsg(String msgSentBy, char msgProtocol, String msgExtra) {
        // common stuff for server & guest to do
        switch (msgProtocol) {
            case Protocol.START_GAME:
                this.onStartGame();
                break;

            case Protocol.TURN_OF:
                this.onTurnOf(msgExtra);
                break;
            default:
                return false; // not recognized
        }
        return true;
    }



    public List<PlayerRowView> getPlayerList() {
        return this.gi.getPlayerList();
    }

    protected void onNewPlayer(String newplayerName) {
        this.gi.updateScoreBoard(newplayerName, 0);
    }

    public void onStartGame() {
        this.gi.onStartGame();
        notifyViewModel(Command.UPDATE_GAME_BOARD);
    }

    public void onTurnOf(String turnOfNickname){
        this.gi.setTurnOfNickname(turnOfNickname);
        if (this.gi.getNickname().equals(turnOfNickname)) { //if it's my turn I play
            //my turn //todo unfreeze board
            //display "Your Turn"
        } else {
            //not my turn //todo freeze board and all presses
            //display "Turn of "nickname"
        }

    }
}
