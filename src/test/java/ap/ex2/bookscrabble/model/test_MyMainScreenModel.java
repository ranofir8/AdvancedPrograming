package ap.ex2.bookscrabble.model;

import ap.ex2.bookscrabble.common.Command2VM;
import ap.ex2.bookscrabble.model.MyMainScreenModel;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Observable;
import java.util.Observer;

public class test_MyMainScreenModel {
    private MyMainScreenModel mainScreenModel;
    private boolean command2VMObserved;
    private boolean displayPortObserved;
    private String errorMessage;

    @BeforeEach
    public void setup() {
        mainScreenModel = new MyMainScreenModel();
        command2VMObserved = false;
        displayPortObserved = false;
        errorMessage = null;
    }

    @Test
    public void testStartGuestGameModel() {
        mainScreenModel.addObserver(new Observer() {
            @Override
            public void update(Observable o, Object arg) {
                if (arg instanceof String[]) {
                    String[] error = (String[]) arg;
                    if (error[0].equals("Unable to establish connection: Connection refused: connect")) {
                        errorMessage = error[1];
                    }
                }
            }
        });

        mainScreenModel.startGuestGameModel("Nickname", "localhost", 1234);

        Assertions.assertNull(errorMessage);
        // Add additional assertions if needed
    }

    @Test
    public void testGetGameStatusText() {
        String statusText = mainScreenModel.getGameStatusText();

        Assertions.assertEquals("No game model.", statusText);
    }
}
