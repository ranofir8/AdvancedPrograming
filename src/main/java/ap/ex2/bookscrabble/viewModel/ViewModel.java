package ap.ex2.bookscrabble.viewModel;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.StringProperty;

import java.util.Observable;
import java.util.Observer;

public abstract class ViewModel extends Observable implements Observer {
    public abstract void startGameModel(); // called when join/host buttons are pressed
}
