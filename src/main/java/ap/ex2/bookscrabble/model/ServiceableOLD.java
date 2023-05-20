package ap.ex2.bookscrabble.model;

public interface ServiceableOLD {
    public void onRecvMessage(String nickName, Byte msgRecv);

    public void sendMessageToGuest(String nickName, Byte msgToSend);
}
