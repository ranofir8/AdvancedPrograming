package ap.ex2.BookScrabbleServer;


import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class GenericServer {
    private final int port;
    private final SimpleClientHandler ch;
    private int threadsLimit;
    private boolean stop;
    private int threadsAmount;

    private ExecutorService es;

    public GenericServer(int port, SimpleClientHandler ch, int threadsLimit) {
        this.port = port;
        this.ch = ch;
        this.threadsLimit = threadsLimit;
        this.stop = true;
    }

    public void start() {
        if (this.stop) {
            this.stop = false;
            new Thread(this::startServer).start();
            this.es = Executors.newFixedThreadPool(this.threadsLimit);  // create new thread pool
        }
    }

    private void startServer() {
        try {
            ServerSocket server = new ServerSocket(this.port);
            server.setSoTimeout(1000); //1s

            while (!this.stop) {
                try {
                    Socket client = server.accept();
                    //Ran temp add
                    System.out.println("Ran temp add");
                    System.out.println(client.getInputStream());

                    SimpleClientHandler newHandler = GenericServer.cloneHandler(this.ch);

                    if (newHandler != null) {
                        Runnable r = () -> {
                            try {
                                newHandler.handleClient(client.getInputStream(), client.getOutputStream());
                                newHandler.close(); // closing streams with client
                                client.close();     // closing client socket
                            } catch (IOException e) {
                                System.out.println("error in new handler");
                                throw new RuntimeException(e);
                            }
                        };
                        this.es.submit(r);
                    } else {
                        System.out.println("Error creating client handler");
                    }
                } catch(SocketTimeoutException ignored) {}
            }
            server.close();  // closing server socket
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            this.es.shutdown(); // close ThreadPool
        }
    }

    public void close() {
        this.stop = true;
    }

    // Creates and returns a new instance of ClientHandler ch type.
    // if any exception occurred, null will be returned.
    // the client handler instance is created with no constructor parameters.
    private static SimpleClientHandler cloneHandler(SimpleClientHandler givenClientHandler) {
        SimpleClientHandler newClientHandler = null;
        try {
            Constructor<?> ctor = givenClientHandler.getClass().getConstructor();
            newClientHandler = (SimpleClientHandler) ctor.newInstance();
        } catch (NoSuchMethodException | InstantiationException | InvocationTargetException | IllegalAccessException e) {
            return null;
        }
        return newClientHandler;
    }
}
