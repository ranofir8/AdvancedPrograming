package ap.ex2.mvvm.model;

public abstract class MainScreenModel extends Model {
    protected GameModel gameModel;

    public abstract void startHostGameModel(String nickname, String savedGameID);
    public abstract void startGuestGameModel(String nickname, String hostIPinput, int hostPortInput);

    public GameModel getGameModel(){return this.gameModel;}

    public abstract String getGameStatusText();
}
