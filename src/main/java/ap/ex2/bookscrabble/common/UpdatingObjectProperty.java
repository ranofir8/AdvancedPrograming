package ap.ex2.bookscrabble.common;

import javafx.beans.property.SimpleBooleanProperty;

import java.util.function.Consumer;

public class UpdatingObjectProperty<T> extends SimpleBooleanProperty {
    private final T payloadObject;

    public UpdatingObjectProperty(T payloadObject) {
        this.payloadObject = payloadObject;
    }

    public UpdatingObjectProperty() {
        this(null);
    }

    public T getObject() {
        return this.payloadObject;
    }

    public void addObjectListener(Consumer<T> changeListener) {
        super.addListener((observableValue, t0, t1) -> changeListener.accept(payloadObject));
        alertChanged();
    }

    public void alertChanged() {
        this.set(!this.get());
    }
}
