package ap.ex2.bookscrabble.view;

import ap.ex2.bookscrabble.viewModel.GameViewModel;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextField;


import java.net.URL;
import java.util.Observable;
import java.util.ResourceBundle;

public class ControllerHelloView implements View, Initializable {
    private SimpleBooleanProperty isHost;
    private GameViewModel gameViewModel;

    @FXML
    private TextField joinGamePort;

    @FXML
    private TextField joinGameIP;

    public ControllerHelloView() {
        System.out.println("controller createed");
        this.isHost = new SimpleBooleanProperty();
    }

    public void initHelloSceneBind() {
        if (this.gameViewModel == null)
            return;
        this.gameViewModel.hostPort.bind(this.joinGamePort.textProperty());
        this.gameViewModel.hostIP.bind(this.joinGameIP.textProperty());
        this.gameViewModel.isHost.bind(this.isHost);
    }

    @Override
    public void update(Observable o, Object arg) {

    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }

    @FXML
    protected void hostNewGameClick() {
        this.isHost.set(true);
        this.gameViewModel.startGameModel();

//        try {
//            this.switchToScene(SCENE_GAME_FXML);
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }
    }

    @FXML
    protected void joinExistingGameClick() {
        this.isHost.set(false); //transmitted to vm and m(?)
        this.gameViewModel.startGameModel();

//        try {
//            this.switchToScene(SCENE_GAME_FXML);
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }
        //setChanged();
        //notifyObservers(false);
        //System.out.println("join");
    }

    public void setGameViewModel(GameViewModel gvm) {
        this.gameViewModel = gvm;
        this.initHelloSceneBind();
    }
}
