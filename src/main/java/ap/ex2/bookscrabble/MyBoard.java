package ap.ex2.bookscrabble;

import ap.ex2.scrabble.Board;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableObjectValue;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;

import java.net.URL;
import java.util.Random;
import java.util.ResourceBundle;

public class MyBoard implements Initializable {
    @FXML
    private Canvas boardCanvas;
    private Board displayedBoard;

    public void drawBoard() {
        Random r = new Random();
        GraphicsContext gc = this.boardCanvas.getGraphicsContext2D();
        int W = (int) this.boardCanvas.getWidth();
        int H = (int) this.boardCanvas.getHeight();
        int x = r.nextInt(W), y = r.nextInt(H);
        gc.strokeRect(x, y, r.nextInt(W-x), r.nextInt(H-y));
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }

    public ObservableValue<? extends Board> getBoardProperty() {
        ObservableObjectValue<Board> oov = new SimpleObjectProperty<>(this.displayedBoard);
        return oov;
    }
}
