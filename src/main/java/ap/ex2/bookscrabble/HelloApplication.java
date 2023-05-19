package ap.ex2.bookscrabble;

import ap.ex2.bookscrabble.model.GameModel;
import ap.ex2.bookscrabble.model.MainScreenModel;
import ap.ex2.bookscrabble.model.MyMainScreenModel;
import ap.ex2.bookscrabble.view.ControllerGameView;
import ap.ex2.bookscrabble.viewModel.GameViewModel;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;


public class HelloApplication extends Application {

    static final String configsFN = "configs.txt";


    @Override
    public void start(Stage stage) throws IOException {
        // this will contain the code
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("hello-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 1200, 800);
        // first screen: choose which type of game
        // 1. host a new game 2. join exist game
        // HostModel

        MainScreenModel gm = new MyMainScreenModel(configsFN);

        //new GameModel()
        GameViewModel gvm = new GameViewModel(gm);
        ControllerGameView gv = fxmlLoader.getController();
        gv.init(gvm);
        gv.setStage(stage);


        stage.setTitle("Hello!");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {


        launch();
    }
}