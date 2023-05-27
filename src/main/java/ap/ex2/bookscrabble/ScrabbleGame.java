package ap.ex2.bookscrabble;

import ap.ex2.bookscrabble.model.MainScreenModel;
import ap.ex2.bookscrabble.model.Model;
import ap.ex2.bookscrabble.model.MyMainScreenModel;
import ap.ex2.bookscrabble.view.ControllerGameView;
import ap.ex2.bookscrabble.view.ControllerHelloView;
import ap.ex2.bookscrabble.view.GameView;
import ap.ex2.bookscrabble.view.View;
import ap.ex2.bookscrabble.viewModel.MyViewModel;
import ap.ex2.bookscrabble.viewModel.ViewModel;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;


public class ScrabbleGame extends Application {
    private View vH;
    private View vG;
    private ViewModel vm;
    private Model m;
    private Stage stage;

    @Override
    public void start(Stage stage) throws IOException {
        // this will contain the code
        FXMLLoader fxmlLoaderH = new FXMLLoader(R.getResource(GameView.SCENE_HELLO_FXML));
        FXMLLoader fxmlLoaderG = new FXMLLoader(R.getResource(GameView.SCENE_GAME_FXML));

        stage.getIcons().add(new Image(ScrabbleGame.class.getResourceAsStream("icon.png")));

        Scene sceneH = new Scene(fxmlLoaderH.load(), 900, 800);
        Scene sceneG = new Scene(fxmlLoaderG.load(), 800, 800);


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

        gm.addObserver(viewModel);
        viewModel.addObserver(gView);
        viewModel.addObserver(hView);

        stage.setTitle("Scrabble Book game");
        viewModel.showHelloScene();

        this.stage = stage;
        this.m = gm;
        this.vm = viewModel;
        this.vH = hView;
        this.vG = gView;

        stage.show();
    }

    private void sendEndSignal() {
        //TODO: send END signal to guests and end the game.
    }

    public static void main(String[] args) {
        launch();
    }
}