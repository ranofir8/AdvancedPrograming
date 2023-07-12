package ap.ex2.mvvm.common;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;

public class ChangeBooleanProperty extends SimpleBooleanProperty {
    @Override
    public void addListener(ChangeListener<? super Boolean> changeListener) {
        super.addListener(changeListener);
        alertChanged();
    }

    public void alertChanged() {
        this.set(!this.get());
    }

    public <T> void changeByProperty(ObservableValue<T> property) {
        property.addListener((observableValue, o, t1) -> alertChanged());
    }
}
