package ap.ex2.bookscrabble.model.host;

import ap.ex2.BookScrabbleServer.BookScrabbleClient;
import ap.ex2.bookscrabble.common.Protocol;
import ap.ex2.bookscrabble.model.GameModel;
import ap.ex2.scrabble.Board;
import ap.ex2.scrabble.Tile;
import ap.ex2.scrabble.Word;

import java.io.IOException;
import java.net.ConnectException;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class HostGameModel extends GameModel implements Observer {
    private final BookScrabbleClient myBookScrabbleClient; //for Client
    private final int hostPort;
    private HostServer hostServer;
    private List<String> playersTurn;  // a list in which the first player has the turn. at the end of his turn his name is moved to the end
    private volatile boolean ignoreDictionary;
    private final HashMap<String, Integer> tilesOfPlayer;

    private GameSave myGameSave;  // puts here data of the game

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
        this.tilesOfPlayer = new HashMap<>();
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

    // send all players which turn it is, and go to the next turn
    private void nextTurn() {
        this.hostServer.sendMsgToAll(Protocol.TURN_OF + this.getCurrentTurnAndCycle());
    }

    private void sendStartingTiles() {
        for (String player : this.playersTurn) {
            this.sendXTiles(player, GameModel.DRAW_START_AMOUNT);
        }
    }

    /**
     * message to the player his tiles.
     * @param player - nickname of a player
     * @param countOfTiles - number of tiles to send
     */
    private void sendXTiles(String player, int countOfTiles) {
        String tilesToDeal = this.dealNTiles(countOfTiles);
        this.tilesOfPlayer.computeIfAbsent(player, s -> 0);
        this.tilesOfPlayer.computeIfPresent(player, (s, tiles) -> tiles + tilesToDeal.length());
        if (tilesToDeal.length() != 0) { // there are no tiles left
            this.hostServer.sendMsgToPlayer(player, Protocol.SEND_NEW_TILES + tilesToDeal);
        } else {
            this.startEndingTheGame();
        }
    }

    /**
     * when the game actually ends or a player leaves during a game this function is called.
     */
    private void startEndingTheGame() {
        // da gaim has kom to en end. tell it to evriwone
        // request from all players how much score they have left
        this.hostServer.sendMsgToAll(Protocol.TURN_OF + "");
        this.hostServer.sendMsgToAll(String.valueOf(Protocol.END_GAME_TILE_SUM_REQUEST));
    }

    private void announceGameEndWinner() {
        // when every player answered with their sum in hand
        String winner = this.getGameInstance().getWinner();
        this.hostServer.sendMsgToAll(Protocol.END_GAME_WINNER + "" + winner);
    }

    /**
     * @param n - number of tiles
     * @return a string of the tiles
     */
    private String dealNTiles(int n) {
        return IntStream.range(0, n).mapToObj(i -> this.getGameInstance().getGameBag().getRand()).filter(Objects::nonNull)
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

    private void createGameSave() {
        // todo - puts all things about the game in a new GameSave object

    }

    // sends data to all players about the new game, and continues the game
    private void loadGameFromSave() {
        // todo
    }

    // *****************************
    //      Protocol handling
    // *****************************

    @Override
    public void update(Observable o, Object arg) { //updates from hostServer, about incoming events from other clients
        if (o == this.hostServer) {
            String[] args = (String[]) arg;
            switch (args[0]) {
                case HostServer.SOCKET_MSG_NOTIFICATION:
                    this.onRecvMessage(args[1], args[2]);
                    break;

                case HostServer.CLIENT_SOCKET_ERROR_NOTIFICATION:
                    this.onClientClosedConnection(args[1]);
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

    private void onClientClosedConnection(String playerName) {
        this.hostServer.sendMsgToAll(Protocol.GAME_CRASH_ERROR + playerName);
        this.closeConnection();
    }

    @Override
    protected boolean handleProtocolMsg(String msgSentBy, char msgProtocol, String msgExtra) {
        boolean hasHandled = super.handleProtocolMsg(msgSentBy, msgProtocol, msgExtra);

        if (msgSentBy == null) //host case
            msgSentBy = this.getGameInstance().getNickname();

//        System.out.println("The player " + msgSentBy + " sent: " + msgExtra);
        switch (msgProtocol) {
            case Protocol.BOARD_ASSIGNMENT_REQUEST:
                Word gottenWord = Word.getWordFromNetworkString(msgExtra, this.getGameInstance().getGameBag());
                this.onBoardAssignment(msgSentBy, gottenWord);
                break;

            case Protocol.BOARD_CHALLENGE_REQUEST:
                this.onChallengeRequest(msgSentBy);
                //System.out.println("He dares to challenge >:D ");
                break;

            case Protocol.SKIP_TURN_REQUEST:
                this.onSkipTurnRequest(msgSentBy);
                break;

            case Protocol.END_GAME_TILE_SUM_RESPONSE:
                int x;
                try {
                    x = Integer.parseInt(msgExtra);
                } catch (NumberFormatException e) {
                    x = 100;
                }
                this.onEndGameTileSumResponse(msgSentBy, x);
                break;
        }

        return hasHandled;
    }

    private void onChallengeRequest(String player) {
        if (this.playerIllegal(player)) //Gilad weird scenarios
            return;

        //challenge the words
        boolean challengeAccepted = Arrays.stream(this.getNotLegalWords()).allMatch(word -> this.myBookScrabbleClient.challengeWord(word));
        //message result to the client
        if (challengeAccepted) {
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

    private boolean playerIllegal(String player) {
        if (!this.getGameInstance().isTurnOf(player)) {
            this.hostServer.sendMsgToPlayer(player, String.valueOf(Protocol.INVALID_ACTION));
            return true;
        }
        return false;
    }

    private void onSkipTurnRequest(String player) {
        if (this.playerIllegal(player)) //Gilad weird scenarios
            return;

        // give him a tile
        sendXTiles(player, 1);
        // next turn
        this.nextTurn();
    }

    private void onEndGameTileSumResponse(String player, int points) {
        // update scoreboard
        if (points < 0) {
            // cheater!!! sum can't be negative
            points = Integer.MAX_VALUE;
        }

        sendUpdateScoreToAll(player, -1 * points);
        this.tilesOfPlayer.remove(player);
        // if the last player sent, announce winner!
        if (this.tilesOfPlayer.isEmpty())
            this.announceGameEndWinner();

    }

    /**
     * messages the client the outcome of sending
     * @param gottenWord
     */
    protected void onBoardAssignment(String player, Word gottenWord) {
        if (this.playerIllegal(player)) //Gilad weird scenarios
            return;

        int scoreOfWord = this.getGameInstance().getGameBoard().tryPlaceWord(gottenWord);
        this.ignoreDictionary = false;

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
                    this.gameInstanceProperty.get().setNotLegalWords(illegals.toArray(new String[]{}));
                    break;
            }
            this.hostServer.sendMsgToPlayer(player, protocolToSend + extra);
        } else {
            this.hostServer.sendMsgToPlayer(player, Protocol.BOARD_ASSIGNMENT_ACCEPTED + "");
            this.hostServer.sendMsgToAll(Protocol.BOARD_UPDATED_BY_ANOTHER_PLAYER + gottenWord.toNetworkString());

            // send player new tiles
            int tileAmount = gottenWord.tileAmount();
            this.tilesOfPlayer.computeIfPresent(player, (s, integer) -> integer - tileAmount);

            int tilesOfPlayer = this.tilesOfPlayer.get(player);
            int tilesToGive = GameModel.DRAW_START_AMOUNT - tilesOfPlayer;
            if (tilesToGive > 0) {
                this.sendXTiles(player, tilesToGive);
            }

            // update scores
            this.sendUpdateScoreToAll(player, scoreOfWord);

            // next turn
            this.nextTurn();
        }

        this.getGameInstance().boardTilesChangeEvent.alertChanged();
    }

    private void sendUpdateScoreToAll(String player, int scoreIncrament) {
        this.hostServer.sendMsgToAll(Protocol.UPDATED_PLAYER_SCORE + "" + scoreIncrament + "," + player);
    }

    @Override
    protected Tile _onGotNewTilesHelper(char tileLetter) {
        return this.getGameInstance().getGameBag().getTileNoRemove(tileLetter);
    }
}
