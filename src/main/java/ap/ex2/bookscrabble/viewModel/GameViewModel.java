package ap.ex2.bookscrabble.viewModel;

import ap.ex2.bookscrabble.common.Command;
import ap.ex2.bookscrabble.model.MainScreenModel;
import ap.ex2.bookscrabble.common.guiMessage;
import javafx.beans.property.*;
import javafx.scene.control.Alert;

import java.util.Observable;

public class GameViewModel extends ViewModel {
    private MainScreenModel myModel;

    // creating/joining game
    public BooleanProperty isHost;
    public StringProperty hostPort;
    public StringProperty hostIP;

    public GameViewModel(MainScreenModel myModel) {
        this.myModel = myModel;

        this.isHost = new SimpleBooleanProperty();
        this.hostPort = new SimpleStringProperty();
        this.hostIP = new SimpleStringProperty();
//        this.isHost.addListener((o, ov, nv) -> {
//            //create a host/guest model?
//            if(nv){
//                //start host?
//            } else {
//                //start guest?
//            }
//        });
    }



    @Override
    public void update(Observable o, Object arg) {
        if (o == this.myModel) {
            if (arg instanceof String[]) {
                String[] args = (String[]) arg;
                if (args[0].equals("MSG")) {
                    setChanged();
                    notifyObservers(new guiMessage(args[1], Alert.AlertType.INFORMATION));
                }
            } else if (arg instanceof Command) {
                setChanged();
                notifyObservers(arg);
            }
        }
    }

    @Override
    public void startGameModel() {
        if (this.isHost.get()) {
            this.myModel.startHostGameModel();
        } else {
            try {
                int hostIntPort = Integer.parseInt(this.hostPort.get());
                this.myModel.startGuestGameModel(this.hostIP.get(), hostIntPort);
            } catch (NumberFormatException e) {
                setChanged();
                notifyObservers(new guiMessage("Host port is invalid!", Alert.AlertType.ERROR));
            }


        }


    }
}
