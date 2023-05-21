package ap.ex2.bookscrabble.view;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class PlayerRowView {
    public StringProperty nickname;

    public PlayerRowView(String ran, String ofir) {
        this.setNickname(ran);
        this.setScore(ofir);
    }

    public PlayerRowView() {

    }

    public void setNickname(String value) { nicknameProperty().set(value); }
    public String getNickname() { return nicknameProperty().get(); }
    public StringProperty nicknameProperty() {
        if (nickname == null) nickname = new SimpleStringProperty(this, "firstName");
        return nickname;
    }

    public StringProperty score;
    public void setScore(String value) { scoreProperty().set(value); }
    public String getScore() { return scoreProperty().get(); }
    public StringProperty scoreProperty() {
        if (score == null) score = new SimpleStringProperty(this, "lastName");
        return score;
    }
}