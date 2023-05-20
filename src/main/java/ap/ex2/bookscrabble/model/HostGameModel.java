package ap.ex2.bookscrabble.model;

import ap.ex2.BookScrabbleServer.BookScrabbleClient;

import java.io.IOException;
import java.util.function.BiConsumer;

public class HostGameModel extends GameModel {
    private BookScrabbleClient myBookScrabbleClient; //for Client
    private int hostPort;
    private HostServer hostServer;



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
        // bind to host port
        this.hostServer = new HostServer(this.hostPort, 5, (Thread t, Throwable e) -> {
            System.out.println("Exception in thread: " + t.getName() + "\n"+ e.getMessage());
        });

        this.hostServer.setOnMsgReceivedCallback(
                new BiConsumer<String, String>() {
                    @Override
                    public void accept(String s, String s2) {
                        onRecvMessage(s, s2);
                    }
                }
        );

        //new BiFunction<>()
        this.hostServer.start();

    }

    public void onRecvMessage(String nickname, String msgRecv) {
        System.out.println("The player " + nickname+" sent: "+msgRecv);
    }

    public void sendMessageToGuest(String nickname, String msgToSend) {

    }




    protected void finalize() {
        this.hostServer.close();
    }


}
