package ap.ex2.mvvm.model;

public interface SocketClientHandler {
    void startHandlingClient();
    void close();
    void sendMsg(String msgToSend);
}
