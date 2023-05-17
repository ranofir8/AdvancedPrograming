package ap.ex2.bookscrabble;

import ap.ex2.scrabble.Board;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;

import java.io.IOException;

public class SmartBoard extends AnchorPane {
    private MyBoard myBoard;
    public ObjectProperty<Board> boardProperty;


    public SmartBoard() {
        super();

        this.boardProperty = new SimpleObjectProperty<>();
        this.boardProperty.bind(this.myBoard.getBoardProperty()); // maybe not here?
        this.myBoard = new MyBoard();
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("MyBoard.fxml"));
        fxmlLoader.setController(this.myBoard);
        try {
            Node n = fxmlLoader.load();
            this.getChildren().add(n);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }


    }
}
