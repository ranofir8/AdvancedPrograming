package ap.ex2.bookscrabble;

import ap.ex2.bookscrabble.model.MainScreenModel;
import ap.ex2.bookscrabble.model.MyMainScreenModel;
import ap.ex2.bookscrabble.view.ControllerGameView;
import ap.ex2.bookscrabble.view.ControllerHelloView;
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
        FXMLLoader fxmlLoader = new FXMLLoader(R.getResource("hello-view.fxml"));
        FXMLLoader fxmlLoader2 = new FXMLLoader(R.getResource("game-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 1200, 800);
        // first screen: choose which type of game
        // 1. host a new game 2. join exist game
        // HostModel

        MainScreenModel gm = new MyMainScreenModel(configsFN);
        GameViewModel gvm = new GameViewModel(gm);

        ControllerGameView gv = fxmlLoader2.getController();
        ControllerHelloView hv = fxmlLoader.getController();

        gv.setGameViewModel(gvm);
        hv.setGameViewModel(gvm);
        //for start i implement it in both controllers (can be done by using abstract controller probably..
        gv.setStage(stage);

        gm.addObserver(gvm);
        gvm.addObserver(gv);




        stage.setTitle("Hello!");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {


        launch();
    }
}