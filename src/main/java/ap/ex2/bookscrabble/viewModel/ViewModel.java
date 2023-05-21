package ap.ex2.bookscrabble.viewModel;

import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.util.Observable;
import java.util.Observer;

public abstract class ViewModel extends Observable implements Observer {
    public abstract void startGameModel(); // called when join/host buttons are pressed
    protected Stage stage;
    private Scene sceneH, sceneG;

    /**
     * save local stages in order to change scenes later
     * @param stage - stage where to put scenes in
     * @param sceneH - scene for Hello window
     * @param sceneG - scene for Game window
     */
    public void setStages(Stage stage, Scene sceneH, Scene sceneG) {
        this.stage = stage;
        this.sceneH = sceneH;
        this.sceneG = sceneG;
    }

    public void showHelloScene() {
        this.setScene(this.sceneH);
    }

    /**
     * @param s the relevant scene (FXML window) to change to
     */
    private void setScene(Scene s) {
        Platform.runLater(()->this.stage.setScene(s));
    }

    public void showGameScene() {
        this.setScene(this.sceneG);
    }
}
