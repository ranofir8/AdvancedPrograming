package ap.ex2.bookscrabble;

import java.util.Observable;

public class GameViewModel extends Observable implements ViewModel {
    private Model myModel;
    public GameViewModel(Model myModel) {
        this.myModel = myModel;
    }

    @Override
    public void update(Observable o, Object arg) {
        //To-Do
    }
}
