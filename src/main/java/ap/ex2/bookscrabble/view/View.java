package ap.ex2.bookscrabble.view;

import java.util.Observer;

// implements Observer
public interface View extends Observer {
    String SCENE_HELLO_FXML = "hello-view.fxml";
    String SCENE_GAME_FXML = "game-view.fxml";
}
