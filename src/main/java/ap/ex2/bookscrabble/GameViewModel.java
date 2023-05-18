package ap.ex2.bookscrabble;

import ap.ex2.bookscrabble.model.Model;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;

import java.util.Observable;

public class GameViewModel extends Observable implements ViewModel {
    private Model myModel;
    public SimpleBooleanProperty isHost;
    public IntegerProperty myPort;
    public GameViewModel(Model myModel) {

        this.myModel = myModel;
        this.isHost = new SimpleBooleanProperty();
        this.isHost.addListener((o,ov,nv)->myModel.isHost=(boolean)nv);
        if(this.isHost.get()) {
            this.myPort = new SimpleIntegerProperty();
            this.myPort.addListener((o,ov,nv) -> myModel.myPort=(int)nv);
        }
        //notify the observer (the model) that we are ready to start a game?
    }

    @Override
    public void update(Observable o, Object arg) {
        //To-Do
    }
}
