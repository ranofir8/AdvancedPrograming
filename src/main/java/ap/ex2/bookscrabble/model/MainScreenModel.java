package ap.ex2.bookscrabble.model;

public abstract class MainScreenModel extends Model {
    protected GameModel gameModel;

    public abstract void startHostGameModel();
    public abstract void startGuestGameModel(String hostIPinput, int hostPortInput);
}
