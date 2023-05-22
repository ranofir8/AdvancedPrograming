package ap.ex2.bookscrabble;

import ap.ex2.bookscrabble.model.MainScreenModel;
import ap.ex2.bookscrabble.model.MyMainScreenModel;
import ap.ex2.bookscrabble.view.ControllerGameView;
import ap.ex2.bookscrabble.view.ControllerHelloView;
import ap.ex2.bookscrabble.view.GameView;
import ap.ex2.bookscrabble.viewModel.MyViewModel;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;


public class HelloApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        // this will contain the code
        FXMLLoader fxmlLoaderH = new FXMLLoader(R.getResource(GameView.SCENE_HELLO_FXML));
        FXMLLoader fxmlLoaderG = new FXMLLoader(R.getResource(GameView.SCENE_GAME_FXML));

        Scene sceneH = new Scene(fxmlLoaderH.load(), 1200, 800);
        Scene sceneG = new Scene(fxmlLoaderG.load(), 1200, 800);


        // first screen: choose which type of game
        // 1. host a new game 2. join exist game
        // HostModel

        MainScreenModel gm = new MyMainScreenModel();
        MyViewModel viewModel = new MyViewModel(gm);
        viewModel.setStages(stage, sceneH, sceneG);

        ControllerGameView gView = fxmlLoaderG.getController();
        ControllerHelloView hView = fxmlLoaderH.getController();

        gView.setGameViewModel(viewModel);
        hView.setGameViewModel(viewModel);

        //for start i implement it in both controllers (can be done by using abstract controller probably..
//        hv.setStage(stage);
//        gv.setStage(stage);

        gm.addObserver(viewModel);
        viewModel.addObserver(gView);
        viewModel.addObserver(hView);
        //cursor parking:
        //|    |    |


        stage.setTitle("Hello!");
        viewModel.showHelloScene();
        stage.show();
    }

    public static void main(String[] args) {


        launch();
    }
}