package ap.ex2.bookscrabble;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;

public class GameView extends View {
    public TextField joinGameIP;
    public TextField joinGamePort;

    @FXML
    private Stage stage;
    private final static String SCENE_HELLO_FXML = "hello-view.fxml";
    private final static String SCENE_GAME_FXML = "game-view.fxml";


    /**
     * @author Ran Ofir
     * join game button function in opening window -
     * opens a game if a valid connection to the inserted ip and port is foun
     */
    @FXML
    protected void joinExistingGameClick() {
        try {
            String ip = joinGameIP.getText();
            String port = joinGamePort.getText();
            this.switchToScene(SCENE_GAME_FXML);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        System.out.println("join");
    }

    public void setStage(Stage stage) {
        this.stage = stage;
        System.out.println("stage is now" + stage);
    }

    /**
     * @author Gilad Savoray
     * @param sceneFXML the relevant scene (FXML window) to change to
     */
    private void switchToScene(String sceneFXML) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource(sceneFXML));

        Scene newScene = new Scene(root);
        this.stage.setScene(newScene);
        this.stage.show();
    }

    /**
     * host game button function in opening window -
     * navigate to a new game with created ip and port
     */
    @FXML
    protected void hostNewGameClick() {
        try {
            this.switchToScene(SCENE_GAME_FXML);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        System.out.println("host");
    }
}