package ap.ex2.bookscrabble.viewModel;

import ap.ex2.bookscrabble.common.Command;
import ap.ex2.bookscrabble.common.Command2VM;
import ap.ex2.bookscrabble.model.GameModel;
import ap.ex2.bookscrabble.model.MainScreenModel;
import ap.ex2.bookscrabble.common.guiMessage;
import javafx.beans.property.*;
import javafx.scene.control.Alert;

import java.util.Observable;

public class MyViewModel extends ViewModel {
    private MainScreenModel myModel;

    // creating/joining game
    public BooleanProperty isHost;
    public StringProperty hostPort;
    public StringProperty hostIP;

    public StringProperty nickname;

    public StringProperty resultHostPort;

    public MyViewModel(MainScreenModel myModel) {
        this.myModel = myModel;

        this.isHost = new SimpleBooleanProperty();
        this.hostPort = new SimpleStringProperty();
        this.hostIP = new SimpleStringProperty();

        this.resultHostPort = new SimpleStringProperty();
        this.nickname = new SimpleStringProperty();
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
            } else if (arg instanceof Command2VM) {
                Command2VM cmd = (Command2VM) arg;
                switch (cmd.command) {
                    case GO_TO_GAME_SCENE:
                        this.showGameScene();
                        break;
                    case DISPLAY_PORT:
                        String s = "port is: "+(int) cmd.args;
                        System.out.println(s);
                        this.resultHostPort.set(s);
                        break;
                    case DEFAULT_GUEST_VALUES:
                        String[] vals = (String[]) cmd.args;
                        this.hostIP.set(vals[0]);
                        this.hostPort.set(vals[0]);
                        System.out.println("got defaults: "+ vals[0] + ", "+vals[1]);
                        break;
                }
            }
        }
    }

    @Override
    public void startGameModel() {
        //nickName handling:
        //port handling:
        if (this.isHost.get()) {
            this.myModel.startHostGameModel(this.nickname.get());
        } else {
            try {
                int hostIntPort = Integer.parseInt(this.hostPort.get());
                this.myModel.startGuestGameModel(this.nickname.get(),this.hostIP.get(), hostIntPort);
            } catch (NumberFormatException e) {
                setChanged();
                notifyObservers(new guiMessage("Host port is invalid!", Alert.AlertType.ERROR));
            }


        }


    }
}
