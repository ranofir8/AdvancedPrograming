package ap.ex2.bookscrabble.common;

import javafx.scene.control.Alert;

public class guiMessage {
    public final String message;
    public final Alert.AlertType alert;

    public guiMessage(String msg, Alert.AlertType alert) {
        this.message = msg;
        this.alert = alert;
    }
}
