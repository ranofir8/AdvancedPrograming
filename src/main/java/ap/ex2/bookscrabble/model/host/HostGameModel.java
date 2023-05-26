package ap.ex2.bookscrabble.model.host;

import ap.ex2.BookScrabbleServer.BookScrabbleClient;
import ap.ex2.bookscrabble.common.Protocol;
import ap.ex2.bookscrabble.common.guiMessage;
import ap.ex2.bookscrabble.model.GameModel;
import ap.ex2.scrabble.Board;
import ap.ex2.scrabble.Tile;
import ap.ex2.scrabble.Word;
import ap.ex2.bookscrabble.common.Command;
import javafx.scene.control.Alert;

import java.io.IOException;
import java.net.ConnectException;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class HostGameModel extends GameModel implements Observer {
    private BookScrabbleClient myBookScrabbleClient; //for Client
    private int hostPort;
    private HostServer hostServer;
    private List<String> playersTurn;  // a list in which the first player has the turn. at the end of his turn his name is moved to the end
    private volatile boolean ignoreDictionary;

    /**
     *  puts in 'playersTurn' the names of the players in turn order
     */
    private void selectTurnOrder() {
        this.playersTurn = new ArrayList<String>(this.hostServer.getOnlinePlayers());
        Collections.shuffle(this.playersTurn);
    }

    public HostGameModel(String nickname, int hostPort, String bookScrabbleSeverIP, int bookScrabbleServerPort) {
        super(nickname); // String name

        this.ignoreDictionary = false;
        this.hostPort = hostPort;
        this.myBookScrabbleClient = new BookScrabbleClient(bookScrabbleSeverIP, bookScrabbleServerPort);
    }


    private void tryPingingBookServer() throws ConnectException {
        if (!this.myBookScrabbleClient.pingServer()) {
            throw new ConnectException("Dictionary book server is offline.");
        }
    }

    @Override
    public int getDisplayPort() {
        return this.hostPort;
    }

    @Override
    public void establishConnection() throws IOException {
        System.out.println("HOST STARTED SERVER");

        this.tryPingingBookServer();

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


    /**
     * [step 1] the host tells everybody the game starts
     */
    public void hostStartGame() { //This happens when the host starts a game
        this.hostServer.sendMsgToAll(Protocol.START_GAME + "");

        // decide on turns randomly
        this.selectTurnOrder();

        // draw tiles for players


        //outter loop of player amount
        //inner loop of tiles to player - 7
        //create a list of 7 random tiles
        this.sendStartingTiles();

        this.nextTurn();
    }

    /**
     * [step 2] The host actually creates the game board
     */
    @Override
    protected void onStartGame() {
        super.onStartGame();  // creates board and init gameInstance
        // pass dictionary check for the host
        this.gameInstanceProperty.get().getGameBoard().setDictioaryCheck(w-> {
            try {
                return ignoreDictionary || myBookScrabbleClient.queryWord(w);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }

    private void nextTurn() {
        this.hostServer.sendMsgToAll(Protocol.TURN_OF + this.getCurrentTurnAndCycle()); //todo they catch it and freeze
    }

    private void sendStartingTiles() {
        for (String player : this.playersTurn) {
            this.sendXTiles(player, GameModel.DRAW_START_AMOUNT);
        }
    }

    private void sendXTiles(String player, int countOfTiles) {
        //message to the player his tiles
        String startingTiles = this.dealNTiles(countOfTiles);
        this.hostServer.sendMsgToPlayer(player, Protocol.SEND_NEW_TILES + startingTiles);
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
            case Protocol.BOARD_CHALLENGE_REQUEST:
                this.onChallengeRequest(msgSentBy);

                //System.out.println("He dares to challenge >:D ");
                break;
        }

        return hasHandled;
    }

    private void onChallengeRequest(String player) {
        //challenge the words
        boolean challengeAccepted = Arrays.stream(this.getNotLegalWords()).allMatch(word -> this.myBookScrabbleClient.challengeWord(word));
        //message result to the client
        if(challengeAccepted) {
            this.ignoreDictionary = true; //this variable makes sure that after a challenge is handled the query window won't show up again

            this.hostServer.sendMsgToPlayer(player, String.valueOf(Protocol.BOARD_ASSIGNMENT_ACCEPTED_CHALLENGE));
            // after challenge attempt accepted, player will query again and *should* be accepted!
        } else {
            this.hostServer.sendMsgToPlayer(player, String.valueOf(Protocol.BOARD_ASSIGNMENT_REJECTED_CHALLENGE));
            // skip the player's turn
            this.onSkipTurnRequest(player);
        }

        //update score accordingly and
        int pts = challengeAccepted ? GameModel.HIT_CHALLENGE_BONUS : GameModel.MISS_CHALLENGE_PENALTY;
        this.sendUpdateScoreToAll(player, pts);


    }

    private void onSkipTurnRequest(String player) {
        // give him a tile

        // next turn
    }

    /**
     * messages the client the outcome of sending
     * @param gottenWord
     */
    protected void onBoardAssignment(String player, Word gottenWord) {
        int scoreOfWord = this.getGameInstance().getGameBoard().tryPlaceWord(gottenWord);
        this.ignoreDictionary = false;
        System.out.println("Score from host: "+ scoreOfWord);
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
                    List<String> illegals = this.getGameInstance().getGameBoard().getNotLegalWords();
                    extra = String.join(",", illegals);
                    break;
            }
            this.hostServer.sendMsgToPlayer(player, protocolToSend + extra);
        } else {
            this.hostServer.sendMsgToPlayer(player, Protocol.BOARD_ASSIGNMENT_ACCEPTED + "");
            this.hostServer.sendMsgToAll(Protocol.BOARD_UPDATED_BY_ANOTHER_PLAYER + gottenWord.toNetworkString());
            // send player new tiles
            this.sendXTiles(player, gottenWord.tileAmount());
            if (player == null) //host case
                player = this.getGameInstance().getNickname();
            // update scores
            this.sendUpdateScoreToAll(player, scoreOfWord);

            // next turn
            this.nextTurn();

        }
    }

    private void sendUpdateScoreToAll(String player, int scoreIncrament) {
        this.hostServer.sendMsgToAll(Protocol.UPDATED_PLAYER_SCORE + "" + scoreIncrament + "," + player);
    }


    @Override
    protected Tile _onGotNewTilesHelper(char tileLetter) {
        return this.getGameInstance().getGameBag().getTileNoRemove(tileLetter);
    }

}
