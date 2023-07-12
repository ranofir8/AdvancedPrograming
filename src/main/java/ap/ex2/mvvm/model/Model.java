package ap.ex2.mvvm.model;

import java.util.Observable;

public abstract class Model extends Observable {
    /**
     * basic observer notification
     * @param message
     */
    protected void notifyViewModel(Object message) {
        setChanged();
        this.notifyObservers(message);
    }
}
