package ap.ex2.bookscrabble.model;

public interface SocketClientHandler {
    void startHandlingClient();
    void close();
    void sendMsg(String msgToSend);
}
