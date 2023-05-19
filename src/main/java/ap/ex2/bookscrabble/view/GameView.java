package ap.ex2.bookscrabble.view;

import ap.ex2.bookscrabble.common.guiMessage;
import ap.ex2.bookscrabble.viewModel.MyViewModel;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.stage.Stage;

public abstract class GameView implements View{
    protected MyViewModel myViewModel;
    @FXML
    protected Stage stage;
    public void setGameViewModel(MyViewModel gvm) {
        this.myViewModel = gvm;
        this.initSceneBind();
    }

    public abstract void initSceneBind();


    public void setStage(Stage stage) {
        this.stage = stage;
    }

    protected void displayMSG(guiMessage messageToDisplay) {
        Alert alert = new Alert(messageToDisplay.alert);
        alert.setTitle("Message");
        //alert.setHeaderText("message");
        alert.setContentText(messageToDisplay.message);
        alert.showAndWait();
    }

}
