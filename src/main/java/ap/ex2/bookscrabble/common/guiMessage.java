package ap.ex2.bookscrabble.common;

import javafx.scene.control.Alert;

public class guiMessage {
    public final String message;
    public final Alert.AlertType alert;
    public final String title;

    public guiMessage(String msg, Alert.AlertType alert, String title) {
        this.message = msg;
        this.alert = alert;
        this.title = title;
    }

    public guiMessage(String msg, Alert.AlertType alert) {
        this(msg, alert, "Message");
    }
}
