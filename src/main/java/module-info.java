module ap.ex2.bookscrabble {
    requires java.naming;
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.media;
    requires org.hibernate.orm.core;
    requires java.sql;
                            
    opens ap.ex2.bookscrabble to javafx.fxml;
    exports ap.ex2.bookscrabble;
    exports ap.ex2.bookscrabble.view;
    opens ap.ex2.bookscrabble.view to javafx.fxml;
    exports ap.ex2.bookscrabble.common;
    opens ap.ex2.bookscrabble.common to javafx.fxml;
    exports ap.ex2.bookscrabble.model.host;
}