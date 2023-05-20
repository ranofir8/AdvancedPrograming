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

    public void setStages(Stage stage, Scene sceneH, Scene sceneG) {
        this.stage = stage;
        this.sceneH = sceneH;
        this.sceneG = sceneG;
    }

    public void showHelloScene() {
        this.setScene(this.sceneH);
    }

    private void setScene(Scene s) {
        Platform.runLater(()->this.stage.setScene(s));
    }

    public void showGameScene() {
        this.setScene(this.sceneG);
    }
}
