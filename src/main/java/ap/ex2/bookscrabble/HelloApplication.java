package ap.ex2.bookscrabble;

import ap.ex2.bookscrabble.common.Protocol;
import ap.ex2.bookscrabble.model.MainScreenModel;
import ap.ex2.bookscrabble.model.MyMainScreenModel;
import ap.ex2.bookscrabble.view.ControllerGameView;
import ap.ex2.bookscrabble.view.ControllerHelloView;
import ap.ex2.bookscrabble.view.GameView;
import ap.ex2.bookscrabble.viewModel.MyViewModel;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Optional;


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

        gm.addObserver(viewModel);
        viewModel.addObserver(gView);
        viewModel.addObserver(hView);

        stage.setTitle("Scrabble Book game");
        viewModel.showHelloScene();

        setCloseConfirmation(stage);
        stage.show();
    }

    public static void setCloseConfirmation(Stage stage) {
        stage.setOnCloseRequest(event -> {
            event.consume();
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Exit Confirmation");
            alert.setHeaderText("Are you sure you want to exit?");
            alert.setContentText("This action will finish the game immediately");

            Optional<ButtonType> result = alert.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                System.out.println("User confirmed exit.");
                stage.close();
            } else {
                // If the user clicks "Cancel", continue
                System.out.println("User canceled exit.");
            }
        });
    }




    void sendEndSignal(MyViewModel vm){
        //TODO: send END signal to guests and end the game.
    }

    public static void main(String[] args) {
        launch();
    }
}