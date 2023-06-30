module ap.ex2.bookscrabble {
    requires java.naming;
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.media;

    requires httpclient;
    requires com.google.gson;
    requires httpcore;
    requires java.ws.rs;
    requires org.hibernate.orm.core;
    requires java.sql;
                            
    opens ap.ex2.bookscrabble to javafx.fxml;
    exports ap.ex2.bookscrabble;
    exports ap.ex2.bookscrabble.view;
    exports ap.ex2.GameScrabbleServer to org.hibernate.orm.core;
    opens ap.ex2.bookscrabble.view to javafx.fxml;
    exports ap.ex2.bookscrabble.common;
    opens ap.ex2.bookscrabble.common to javafx.fxml;
    exports ap.ex2.bookscrabble.model.host;
    exports ap.ex2.GameScrabbleServer.Saves;
}