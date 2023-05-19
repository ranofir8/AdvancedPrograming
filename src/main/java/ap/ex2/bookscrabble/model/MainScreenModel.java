package ap.ex2.bookscrabble.model;

public abstract class MainScreenModel extends Model {
    protected GameModel gameModel;

    public abstract void startGameModel(boolean isHost);
}
