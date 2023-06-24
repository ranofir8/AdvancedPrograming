package ap.ex2.bookscrabble.view;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class PlayerTableRow {
    public StringProperty nickname;

    public PlayerTableRow(String nickname, int score) {
        this.setNickname(nickname);
        this.setScore(score);
    }

    public void setNickname(String value) { nicknameProperty().set(value); }
    public String getNickname() { return nicknameProperty().get(); }
    public StringProperty nicknameProperty() {
        if (nickname == null) nickname = new SimpleStringProperty(this, "nickname");
        return nickname;
    }

    public IntegerProperty score;
    public void setScore(int value) { scoreProperty().set(value); }
    public int getScore() { return scoreProperty().get(); }
    public IntegerProperty scoreProperty() {
        if (score == null) score = new SimpleIntegerProperty(this, "score");
        return score;
    }
}