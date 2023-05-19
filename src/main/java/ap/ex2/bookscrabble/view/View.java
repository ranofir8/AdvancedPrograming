package ap.ex2.bookscrabble.view;

import java.util.Observable;
import java.util.Observer;

// implements Observer
public interface View extends Observer {
    public final static String SCENE_HELLO_FXML = "hello-view.fxml";
    public final static String SCENE_GAME_FXML = "game-view.fxml";

}
