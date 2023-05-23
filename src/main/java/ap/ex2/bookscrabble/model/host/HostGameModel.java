package ap.ex2.bookscrabble.model.host;

import ap.ex2.BookScrabbleServer.BookScrabbleClient;
import ap.ex2.bookscrabble.common.Protocol;
import ap.ex2.bookscrabble.model.GameModel;
import ap.ex2.scrabble.Tile;

import java.io.IOException;
import java.util.*;

public class HostGameModel extends GameModel implements Observer {
    private BookScrabbleClient myBookScrabbleClient; //for Client
    private int hostPort;
    private HostServer hostServer;
    private Tile.Bag gameBag;
    private List<String> playersTurn;  // a list in which the first player has the turn. at the end of his turn his name is moved to the end

    /**
     *  puts in 'playersTurn' the names of the players in turn order
     */
    private void shuffleTurns() {
        this.playersTurn = new ArrayList<String>(this.hostServer.getOnlinePlayers());
        Collections.shuffle(this.playersTurn);
    }
    public HostGameModel(String nickname, int hostPort, String bookScrabbleSeverIP, int bookScrabbleServerPort) {
        super(nickname); //String configFileName
        this.hostPort = hostPort;

        this.myBookScrabbleClient = new BookScrabbleClient(bookScrabbleSeverIP, bookScrabbleServerPort);
    }

    @Override
    public int getDisplayPort() {
        return this.hostPort;
    }

    @Override
    public void establishConnection() throws IOException {
        System.out.println("HOST STARTED SERVER");
        // bind to host port
        this.hostServer = new HostServer(this.hostPort, 5, (Thread t, Throwable e) -> {
            System.out.println("Exception in thread: " + t.getName() + "\n"+ e.getMessage());
        });
        this.hostServer.addObserver(this);

        this.hostServer.setMyNickname(this.gi.getNickname());

        this.hostServer.start();
    }

    @Override
    protected void closeConnection() {
        this.hostServer.close();
    }

    @Override
    public void onStartGame() { //This happens when the host starts a game
        super.onStartGame();  // status PLAYING

//        notifyViewModel(Command.UPDATE_GAME_BOARD);
        this.hostServer.sendMsgToAll(Protocol.START_GAME + "");

        // decide on turns randomly
        this.shuffleTurns();
        this.hostServer.sendMsgToAll(Protocol.TURN_OF + this.getCurrentTurnAndCycle()); //todo they catch it and freeze
//        this.onTurnOf();

        // draw tiles for players
    }

    /**
     *
     * @return the player with the current turn,
     * and put him in the end of the turn list (update turns for next times)
     */
    private String getCurrentTurnAndCycle() {
        String playerNickname = this.playersTurn.remove(0);
        this.playersTurn.add(playerNickname);
        return playerNickname;
    }

    /**
     * Send to all player whether it's their turn or not
     */
    private void sendTurnsToPlayers() {
        String currentTurn = this.playersTurn.get(0);

    }

    protected void finalize() {
        this.hostServer.close();
    }




    @Override
    public void update(Observable o, Object arg) { //updates from hostServer, about incoming events from other clients
        if (o == this.hostServer) {
            String[] args = (String[]) arg;
            switch (args[0]) {
                case HostServer.SOCKET_MSG_NOTIFICATION:
                    this.onRecvMessage(args[1], args[2]);
                    break;
                case HostServer.HOST_LOOPBACK_MSG_NOTIFICATION:
                    this.onRecvMessage(null, args[1]);
                    break;
                case HostServer.PLAYER_JOINED_NOTIFICATION:
                    //controllerGameView?
                    this.onNewPlayer(args[1]);
                    break;
                case HostServer.PLAYER_EXITED_NOTIFICATION:
                    this.gi.removeScoreBoardPlayer(args[1]);
                    break;
            }

        }
    }

    @Override
    protected void handleProtocolMsg(String msgSentBy, char msgProtocol, String msgExtra) {
        super.handleProtocolMsg(msgSentBy, msgProtocol, msgExtra);

        System.out.println("The player " + msgSentBy + " sent: " + msgExtra);
    }

}
