package ap.ex2.bookscrabble.model.host;

import ap.ex2.bookscrabble.common.Protocol;
import ap.ex2.bookscrabble.model.GameModel;
import ap.ex2.bookscrabble.model.SocketClientHandler;
import ap.ex2.bookscrabble.model.MyClientHandler;

import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.HashMap;
import java.util.Observable;
import java.util.Observer;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.BiConsumer;

public class HostServer extends Observable implements Observer {
    public static final String SOCKET_MSG_NOTIFICATION = "socketMsg";  // used when a guest sends a message to itself
    public static final String HOST_LOOPBACK_MSG_NOTIFICATION = "hostMsg";  // used when the host sends a message to itself
    public static final String PLAYER_JOINED_NOTIFICATION = "playerJoin";
    public static final String PLAYER_EXITED_NOTIFICATION = "playerExit";

    private final int hostPort;
    private final int threadsLimit;
    private boolean stop;

    private Thread.UncaughtExceptionHandler exceptionHandler;

    public Set<String> getOnlinePlayers() {return this.playersSockets.keySet();}

    private HashMap<String, SocketClientHandler> playersSockets;

    private ExecutorService es;

    public HostServer(int port, int threadsLimit, Thread.UncaughtExceptionHandler exceptionHandler) {
        this.hostPort = port;
        this.threadsLimit = threadsLimit;
        this.stop = true;
        this.exceptionHandler = exceptionHandler;
        this.playersSockets = new HashMap<>();
    }

    // send a message to a specific guest
    public void sendMsgToPlayer(String nickName, String msgToSend) {
        if (this.playersSockets.get(nickName) != null) {
            this.playersSockets.get(nickName).sendMsg(msgToSend);
        } else {
            setChanged();
            notifyObservers(new String[]{HostServer.HOST_LOOPBACK_MSG_NOTIFICATION, msgToSend});
        }
    }

    // send a message to all online players EXCLUDING HOST
    public void sendMsgToAllGuests(String msgToSend) {
        for (String n : this.playersSockets.keySet())
            sendMsgToGuest(n, msgToSend);
    }

    // send a message to all online players, including HOST!
    public void sendMsgToAll(String msgToSend) {
        this.sendMsgToAllGuests(msgToSend);
        // send to host model too!
        setChanged();
        notifyObservers(new String[]{HostServer.HOST_LOOPBACK_MSG_NOTIFICATION, msgToSend});
    }

    public void start() {
        if (this.stop) {
            this.stop = false;
            Thread t = new Thread(this::startServer);
            t.setUncaughtExceptionHandler(this.exceptionHandler);
            t.start();
            this.es = Executors.newFixedThreadPool(this.threadsLimit);  // create new thread pool
        }
    }

    private void startServer() {
        try {
            ServerSocket server = new ServerSocket(this.hostPort);
            server.setSoTimeout(1000); //1s

            while (!this.stop) {
                try {
                    Socket client = server.accept();

                    MyClientHandler clientHandler = new MyClientHandler(client);  // todo on a new thread?
                    clientHandler.addObserver(this);  // listen to notifications from this client
                    clientHandler.startHandlingClient();

                } catch(SocketTimeoutException ignored) {}
            }
            server.close();  // closing server socket
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            this.es.shutdown(); // close ThreadPool
        }
    }

    // got a message from one of the clients
    @Override
    public void update(Observable o, Object arg) {
        MyClientHandler client = (MyClientHandler) o;
        String[] nickname = {null}; //array in order to change in forEach loop
        this.playersSockets.forEach((s, socketClientHandler) -> {
            if (socketClientHandler == client) {
                nickname[0] = s; //the nickname is found and set
            }
        });

        if (nickname[0] != null) {
            // client socket is already recognized, just forward this message to the Host model
            setChanged();
            notifyObservers(new String[]{HostServer.SOCKET_MSG_NOTIFICATION, nickname[0], (String) arg});
        } else {
            // client socket is new!

            String sentMsg = (String) arg;

            char msgProtocol = sentMsg.charAt(0);
            String msgExtra = sentMsg.substring(1, sentMsg.length());

            if (msgProtocol != Protocol.GUEST_LOGIN_REQUEST) {
                // reject socket because he is not playing by the protocol
                client.close();
            }

            String chosenNickname = msgExtra;

            if(this.playersSockets.size() >= GameModel.MAX_PLAYERS) {
                client.sendMsg(Protocol.HOST_LOGIN_REJECT_FULL + "");
                client.close();
            } else if (this.hasPlayerNamed(chosenNickname)) {
                // kick this player
                client.sendMsg(Protocol.HOST_LOGIN_REJECT_NICKNAME + "");
                client.close();

            } else {
                // welcome him to the game

                // add new player to host's screen (locally)
                setChanged();
                notifyObservers(new String[]{HostServer.PLAYER_JOINED_NOTIFICATION, chosenNickname}); // todo update when a player leaves!

                // send new player's name to the other players
                this.sendMsgToAllGuests(Protocol.PLAYER_UPLOAD + chosenNickname);

                // add new player to the server
                Set<String> oldPlayers = this.playersSockets.keySet();
                this.playersSockets.put(chosenNickname, client);

                // pass on the old players and send their names to the new player (for updating his scoreboard)
                client.sendMsg(Protocol.HOST_LOGIN_ACCEPT + "");
                for (String oldPlayerName : oldPlayers) {
                    this.sendMsgToGuest(chosenNickname, Protocol.PLAYER_UPLOAD + oldPlayerName);
                }
            }
        }
    }

    public void close() {
        this.stop = true;
    }

    public boolean hasPlayerNamed(String nickName) {
        return this.playersSockets.containsKey(nickName);
    }

    /**
     * this method adds a pair of <nickname, null> to the playerSockets map.
     *  this null dummy value allows the model to know how the host is called and prevent guests from getting this name
     * @param nickname
     */
    public void setMyNickname(String nickname) {
        this.playersSockets.put(nickname, null);
    }
}

