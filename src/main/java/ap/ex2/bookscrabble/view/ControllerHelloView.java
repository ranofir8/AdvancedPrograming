package ap.ex2.bookscrabble.view;

import ap.ex2.bookscrabble.Config;
import ap.ex2.bookscrabble.R;
import ap.ex2.bookscrabble.common.Command;
import ap.ex2.bookscrabble.common.guiMessage;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;


import java.net.URISyntaxException;
import java.net.URL;
import java.util.Observable;
import java.util.ResourceBundle;

public class ControllerHelloView extends GameView implements Initializable {


    public BorderPane helloBG;
    protected final SimpleBooleanProperty isHostHello;

    @FXML
    private TextField joinGamePortTF;

    @FXML
    private TextField joinGameIPTF;

    @FXML
    private TextField nicknameTF;

    @FXML
    private Button joinGameButton;
    @FXML
    private Button hostGameButton;
    @FXML
    private Button hostSavedGameButton;

    public ControllerHelloView() {

        this.isHostHello = new SimpleBooleanProperty();

    }

    public void initSceneBind() {
        if (this.myViewModel == null)
            return;
        this.myViewModel.hostPort.bind(this.joinGamePortTF.textProperty());
        this.myViewModel.hostIP.bind(this.joinGameIPTF.textProperty());
        this.myViewModel.isHost.bind(this.isHostHello);
        this.myViewModel.nicknamePropertyTextField.bind(this.nicknameTF.textProperty());
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
       /*try {
            this.helloBG.setCenter(new ImageView(new Image(R.getResource("bg.png").toURI().toString())));
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }*/
        try {
            this.helloBG.setStyle("-fx-background-image: url("+ R.getResource("bg.png").toURI() +");" +
                    "-fx-background-size: cover;");
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }

        String defaultGuestIP = Config.getInstance().get(Config.DEFAULT_GUEST_IP_KEY);
        String defaultGuestPort = Config.getInstance().get(Config.DEFAULT_GUEST_PORT_KEY);

        if (defaultGuestIP != null)
            this.joinGameIPTF.setText(defaultGuestIP);
        if (defaultGuestPort != null)
            this.joinGamePortTF.setText(defaultGuestPort);

        this.joinGameButton.disableProperty().bind(this.hostGameButton.disableProperty());
        this.hostSavedGameButton.disableProperty().bind(this.hostGameButton.disableProperty());

        this.nicknameTF.textProperty().addListener((observableValue, oldNickname, newNickname) ->
            this.hostGameButton.setDisable(newNickname.length() == 0));

        this.hostGameButton.disableProperty().set(true);
    }

    @FXML
    protected void hostNewGameClick() {
        this.isHostHello.set(true);
        this.myViewModel.startGameModel();
    }

    @FXML
    protected void hostSavedGameClick() {
        // todo: implement
    }

    /**
     * @author Ran Ofir
     * join game button function in opening window -
     * opens a game if a valid connection to the inserted ip and port is foun
     */
    @FXML
    protected void joinExistingGameClick() {
        this.isHostHello.set(false); //transmitted to vm and m(?)
        this.myViewModel.startGameModel();
    }
}
