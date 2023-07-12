package ap.ex2.mvvm.view;

import ap.ex2.mvvm.common.guiMessage;
import ap.ex2.mvvm.viewModel.MyViewModel;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.stage.Stage;

public abstract class GameView implements View {
    protected MyViewModel myViewModel;



    @FXML
    protected Stage stage;

    public void setGameViewModel(MyViewModel gvm) {
        this.myViewModel = gvm;
        this.initSceneBind();
    }

    public abstract void initSceneBind();

    protected void displayMSG(guiMessage messageToDisplay) {
        Platform.runLater(() -> {
            Alert alert = new Alert(messageToDisplay.alert);
            if (messageToDisplay.alert == Alert.AlertType.ERROR)
                SoundManager.singleton.playSound(SoundManager.SOUND_OF_FAILURE);
            alert.setTitle(messageToDisplay.title);
            alert.setHeaderText(messageToDisplay.title);
            alert.setContentText(messageToDisplay.message);
            alert.showAndWait();
        });
    }
}
