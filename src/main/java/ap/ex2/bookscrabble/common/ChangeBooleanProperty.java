package ap.ex2.bookscrabble.common;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ChangeListener;

public class ChangeBooleanProperty extends SimpleBooleanProperty {
    @Override
    public void addListener(ChangeListener<? super Boolean> changeListener) {
        super.addListener(changeListener);
        alertChanged();
    }

    public void alertChanged() {
        this.set(!this.get());
    }
}
