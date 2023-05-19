package ap.ex2.bookscrabble.viewModel;

import ap.ex2.bookscrabble.model.MainScreenModel;
import ap.ex2.bookscrabble.view.guiMessage;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;

import java.util.Observable;

public class GameViewModel extends ViewModel {
    private MainScreenModel myModel;

    // creating/joining game
    public BooleanProperty isHost;
    public IntegerProperty hostPort;

    public GameViewModel(MainScreenModel myModel) {
        this.myModel = myModel;

        this.isHost = new SimpleBooleanProperty();
        this.hostPort = new SimpleIntegerProperty();
        this.isHost.addListener((o, ov, nv) -> {
            //create a host/guest model?
        });


//
//        this.isHost.addListener((o, ov, nv) -> {
//            myModel.isHost=(boolean) nv;
//            if(nv){
//                System.out.println("Host is opened, with port: "+this.hostPort.get());
//            }
//        });
//        this.hostPort.addListener((ob, ov, nv) -> this.myModel.myPort=(int)nv);

        //notify the observer (the model) that we are ready to start a game?
    }

    @Override
    public void update(Observable o, Object arg) {
        if (o == this.myModel) {
            if (arg instanceof String[]) {
                String[] args = (String[]) arg;
                if (args[0].equals("MSG")) {
                    notifyObservers(new guiMessage(args[1]));
                }
            }
        }
    }
}
