package ap.ex2.bookscrabble.model;

import ap.ex2.bookscrabble.common.Command;
import ap.ex2.bookscrabble.common.Protocol;
import ap.ex2.bookscrabble.view.PlayerRowView;
import ap.ex2.scrabble.Tile;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import java.util.List;

public abstract class GameModel extends Model {
    public static final int MIN_PLAYERS = 2;
    public static final int MAX_PLAYERS = 4;
    public static final int DRAW_START_AMOUNT = 7;
    public ObjectProperty<GameInstance> gameInstanceProperty;

    public GameModel(String nickname) {
        this.gameInstanceProperty = new SimpleObjectProperty<>();
        this.gameInstanceProperty.set(new GameInstance(nickname));
    }

    public GameInstance getGameInstance() {
        return this.gameInstanceProperty.get();
    }

    public abstract int getDisplayPort();

    public abstract void establishConnection() throws Exception;

    protected abstract void closeConnection();

    protected void onRecvMessage(String sentBy, String msgContent) {
        // nickname will always be null, because only the host can send message to guests
        char msgProtocol = msgContent.charAt(0); //when a message is sent, the first part is a protocol
        String msgExtra = msgContent.substring(1); //rest of the message contain details

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
            case Protocol.SEND_NEW_TILES:
                this.onGotNewTiles(msgExtra); //msgExtra contains the tiles
                break;
            default:
                return false; // not recognized
        }
        return true;
    }

    private void onGotNewTiles(String tilesGotten) {
        for (char tileLetter : tilesGotten.toCharArray()) {
            // take from Bag and add to hand, at the end update board in GUI
            this.getGameInstance().getPlayerStatus().addTile(this._onGotNewTilesHelper(tileLetter));
        }
        notifyViewModel(Command.UPDATE_GAME_TILES);
    }

    protected abstract Tile _onGotNewTilesHelper(char tileLetter);




    public List<PlayerRowView> getPlayerList() {
        return this.getGameInstance().getPlayerList();
    }

    protected void onNewPlayer(String newPlayerName) {
        this.getGameInstance().updateScoreBoard(newPlayerName, 0);
    }

    public void onStartGame() {
        this.getGameInstance().onStartGame();
        notifyViewModel(Command.UPDATE_GAME_BOARD);
    }

    public void onTurnOf(String turnOfNickname){
        this.getGameInstance().setTurnOfNickname(turnOfNickname);
    }
}
