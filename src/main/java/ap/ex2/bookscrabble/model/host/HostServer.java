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

                    HostClientHandler clientHandler = new HostClientHandler(client);  // todo on a new thread?
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
        HostClientHandler client = (HostClientHandler) o;
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
            // client is new!

        }

        if (this.onMsgReceivedConsumer != null)
            this.onMsgReceivedConsumer.accept(nickname[0], (String) arg);
    }

    public void close() {
        this.stop = true;
    }

}

