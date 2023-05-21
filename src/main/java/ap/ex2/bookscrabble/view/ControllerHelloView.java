package ap.ex2.bookscrabble.view;

import ap.ex2.bookscrabble.Config;
import ap.ex2.bookscrabble.common.Command;
import ap.ex2.bookscrabble.common.guiMessage;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextField;


import java.net.URL;
import java.util.Observable;
import java.util.ResourceBundle;

public class ControllerHelloView extends GameView implements Initializable {
    private SimpleBooleanProperty isHost;

    @FXML
    private TextField joinGamePort;

    @FXML
    private TextField joinGameIP;

    @FXML
    private TextField nickname;

    public ControllerHelloView() {
        this.isHost = new SimpleBooleanProperty();
    }

    public void initSceneBind() {
        if (this.myViewModel == null)
            return;
        this.myViewModel.hostPort.bind(this.joinGamePort.textProperty());
        this.myViewModel.hostIP.bind(this.joinGameIP.textProperty());
        this.myViewModel.isHost.bind(this.isHost);
        this.myViewModel.nickname.bind(this.nickname.textProperty());
    }

    @Override
    public void update(Observable o, Object arg) {
        if (o == this.myViewModel) {
            if (arg instanceof guiMessage) {
                this.displayMSG((guiMessage) arg);
            } else if (arg instanceof Command) {
//                this.switchToScene(SCENE_GAME_FXML);
                this.initSceneBind();
            }
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        String defaultGuestIP = Config.getInstance().get(Config.DEFAULT_GUEST_IP_KEY);
        String defaultGuestPort = Config.getInstance().get(Config.DEFAULT_GUEST_PORT_KEY);

        if (defaultGuestIP != null)
            this.joinGameIP.setText(defaultGuestIP);
        if (defaultGuestPort != null)
            this.joinGamePort.setText(defaultGuestPort);
    }

    @FXML
    protected void hostNewGameClick() {
        this.isHost.set(true);
        this.myViewModel.startGameModel();

//        try {
//            this.switchToScene(SCENE_GAME_FXML);
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }
    }

    /**
     * @author Ran Ofir
     * join game button function in opening window -
     * opens a game if a valid connection to the inserted ip and port is foun
     */
    @FXML
    protected void joinExistingGameClick() {
        this.isHost.set(false); //transmitted to vm and m(?)
        this.myViewModel.startGameModel();
    }
}
