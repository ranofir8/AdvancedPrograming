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
        this.stage.setScene(this.sceneH);
    }

    public void showGameScene() {
        this.stage.setScene(this.sceneG);
    }
}
