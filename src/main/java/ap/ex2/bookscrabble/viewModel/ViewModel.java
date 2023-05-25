package ap.ex2.bookscrabble.viewModel;

import ap.ex2.bookscrabble.model.MainScreenModel;
import ap.ex2.bookscrabble.model.Model;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.stage.Stage;

import java.util.Observable;
import java.util.Observer;
import java.util.Optional;

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
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Exit Confirmation");
            alert.setHeaderText("Are you sure you want to exit?");
            alert.setContentText("This action will finish the game immediately");

            Optional<ButtonType> result = alert.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                System.out.println("User confirmed exit.");
//                stage.close();

                // todo shutdown client handler/server handler
            } else {
                // If the user clicks "Cancel", continue
                System.out.println("User canceled exit.");
            }
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
