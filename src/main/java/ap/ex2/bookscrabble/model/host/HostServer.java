package ap.ex2.bookscrabble.model.host;

import ap.ex2.bookscrabble.common.Protocol;
import ap.ex2.bookscrabble.model.SocketClientHandler;
import ap.ex2.bookscrabble.model.MyClientHandler;

import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.HashMap;
import java.util.Observable;
import java.util.Observer;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.BiConsumer;

public class HostServer implements Observer {
    private final int hostPort;
    private final int threadsLimit;
    private boolean stop;

    private Thread.UncaughtExceptionHandler exceptionHandler;
    private HashMap<String, SocketClientHandler> playersSockets;

    private BiConsumer<String, String> onMsgReceivedConsumer;

    private ExecutorService es;

    public HostServer(int port, int threadsLimit, Thread.UncaughtExceptionHandler exceptionHandler) {
        this.hostPort = port;
        this.threadsLimit = threadsLimit;
        this.stop = true;
        this.exceptionHandler = exceptionHandler;
        this.playersSockets = new HashMap<>();
    }

    public void setOnMsgReceivedCallback(BiConsumer<String, String> func) {
        this.onMsgReceivedConsumer = func;
    }

    public void sendMsgToGuest(String nickName, String msgToSend) {
        this.playersSockets.get(nickName).sendMsg(msgToSend);
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
            if (this.onMsgReceivedConsumer != null)
                this.onMsgReceivedConsumer.accept(nickname[0], (String) arg);
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


            if (this.hasPlayerNamed(chosenNickname)) {

                // kick this player
                client.sendMsg(Protocol.HOST_LOGIN_REJECT + "");
                client.close();

            } else {
                // welcome him to the game
                this.playersSockets.put(chosenNickname, client);
                client.sendMsg(Protocol.HOST_LOGIN_ACCEPT + "");
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
