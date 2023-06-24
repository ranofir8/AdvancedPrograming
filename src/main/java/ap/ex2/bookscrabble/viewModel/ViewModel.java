package ap.ex2.bookscrabble.viewModel;

import ap.ex2.bookscrabble.model.MainScreenModel;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.util.Observable;
import java.util.Observer;

import static java.lang.System.exit;

public abstract class ViewModel extends Observable implements Observer {
    public abstract void startGameModel(); // called when join/host buttons are pressed
    protected Stage stage;
    private Scene sceneH, sceneG;

    protected MainScreenModel myModel;

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

        this.stage.setOnCloseRequest(event -> {
            event.consume();
            exit(0);
        });
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
        Platform.runLater(() -> {
            this.setScene(this.sceneG);
            this.stage.setTitle(this.getTitleText());
        });
    }

    protected abstract String getTitleText();

    public abstract void startGame();

    protected abstract void initGameModelBinds();

    protected abstract void sendWord();

    protected abstract void requestChallenge();

    protected abstract void giveUpTurn();

}
