package ap.ex2.bookscrabble;

import ap.ex2.bookscrabble.model.GameModel;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class HelloApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        // this will contain the code
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("hello-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 1200, 800);
        // first screen: choose which type of game
        // 1. host a new game 2. join exist game
        // HostModel

        GameModel gm = null;
        //new GameModel()
        GameViewModel gvm = new GameViewModel(gm);
        GameView gv = fxmlLoader.getController();
        gv.setStage(stage);

        stage.setTitle("Hello!");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {


        launch();
    }
}