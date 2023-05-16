package ap.ex2.bookscrabble;

import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class GameView extends View {
    @FXML
    private Label welcomeText;

    @FXML
    protected void onHelloButtonClick() {
        welcomeText.setText("Welcome to JavaFX Application!");
    }
}