package ap.ex2.bookscrabble.model.host;

import ap.ex2.BookScrabbleServer.BookScrabbleClient;
import ap.ex2.bookscrabble.common.Protocol;
import ap.ex2.bookscrabble.model.GameModel;
import ap.ex2.scrabble.Board;
import ap.ex2.scrabble.Tile;
import ap.ex2.scrabble.Word;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class HostGameModel extends GameModel implements Observer {
    private BookScrabbleClient myBookScrabbleClient; //for Client
    private int hostPort;
    private HostServer hostServer;
    private List<String> playersTurn;  // a list in which the first player has the turn. at the end of his turn his name is moved to the end

    /**
     *  puts in 'playersTurn' the names of the players in turn order
     */
    private void selectTurnOrder() {
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
        this.hostServer = new HostServer(this.hostPort, GameModel.MAX_PLAYERS+1, (Thread t, Throwable e) -> {
            System.out.println("Exception in thread: " + t.getName() + "\n"+ e.getMessage());
        });
        this.hostServer.addObserver(this);

        this.hostServer.setMyNickname(this.getGameInstance().getNickname());

        this.hostServer.start();
    }

    @Override
    protected void closeConnection() {
        this.hostServer.close();
    }

    @Override
    protected void sendMsgToHost(String msg) {
        this.hostServer.sendMsgToPlayer(null, msg);
    }


    public void hostStartGame() { //This happens when the host starts a game
        this.hostServer.sendMsgToAll(Protocol.START_GAME + "");

        // decide on turns randomly
        this.selectTurnOrder();

        // draw tiles for players


        //outter loop of player amount
        //inner loop of tiles to player - 7
        //create a list of 7 random tiles
        this.sendStartingTiles();

        //todo in loop until the end of the game?

        this.hostServer.sendMsgToAll(Protocol.TURN_OF + this.getCurrentTurnAndCycle()); //todo they catch it and freeze

    }

    private void sendStartingTiles() {
        for (String player : this.playersTurn) {
            String startingTiles = this.dealNTiles(GameModel.DRAW_START_AMOUNT);
            //message to the player his tiles
            this.hostServer.sendMsgToPlayer(player, Protocol.SEND_NEW_TILES + startingTiles);
        }
    }

    private String dealNTiles(int n) {
        // todo what to do if the bank is empty?
        return IntStream.range(0, n).mapToObj(i -> this.getGameInstance().getGameBag().getRand())
                .map(t -> t.letter).map(String::valueOf).collect(Collectors.joining());
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
                    this.getGameInstance().removeScoreBoardPlayer(args[1]);
                    break;
            }

        }
    }

    @Override
    protected boolean handleProtocolMsg(String msgSentBy, char msgProtocol, String msgExtra) {
        boolean hasHandled = super.handleProtocolMsg(msgSentBy, msgProtocol, msgExtra);

        System.out.println("The player " + msgSentBy + " sent: " + msgExtra);
        switch (msgProtocol) {
            case Protocol.BOARD_ASSIGNMENT_REQUEST:
                Word gottenWord = Word.getWordFromNetworkString(msgExtra, this.getGameInstance().getGameBag());
                this.onBoardAssignment(msgSentBy, gottenWord);
                break;
        }

        return hasHandled;
    }


    /**
     * messages the client the outcome of sending
     * @param gottenWord
     */
    protected void onBoardAssignment(String player, Word gottenWord) {
        int scoreOfWord = this.getGameInstance().getGameBoard().tryPlaceWord(gottenWord);
        char protocolToSend = ' ';
        String extra = "";
        if (scoreOfWord < 0) {
            switch (scoreOfWord) {
                case Board.CHECK_OUTSIDE_BOARD_LIMITS:
                    protocolToSend = Protocol.ERROR_OUTSIDE_BOARD_LIMITS;
                    break;
                case Board.CHECK_NOT_ON_STAR:
                    protocolToSend = Protocol.ERROR_NOT_ON_STAR;
                    break;
                case Board.CHECK_NOT_LEANS_ON_EXISTING_TILES:
                    protocolToSend = Protocol.ERROR_NOT_LEANS_ON_EXISTING_TILES;
                    break;
                case Board.CHECK_NOT_MATCH_BOARD:
                    protocolToSend = Protocol.ERROR_NOT_MATCH_BOARD;
                    break;
                case Board.CHECK_WORD_NOT_LEGAL:
                    protocolToSend = Protocol.ERROR_WORD_NOT_LEGAL;
                    break;
            }
            this.hostServer.sendMsgToPlayer(player, protocolToSend + "");
        } else {
            this.hostServer.sendMsgToPlayer(player, Protocol.BOARD_ASSIGNMENT_ACCEPTED + "");
            this.hostServer.sendMsgToAll(Protocol.BOARD_UPDATED_BY_ANOTHER_PLAYER + gottenWord.toNetworkString());
            this.hostServer.sendMsgToAll(Protocol.UPDATED_PLAYER_SCORE + scoreOfWord + "," + player);

        }
    }


    @Override
    protected Tile _onGotNewTilesHelper(char tileLetter) {
        return this.getGameInstance().getGameBag().getTileNoRemove(tileLetter);
    }
}
