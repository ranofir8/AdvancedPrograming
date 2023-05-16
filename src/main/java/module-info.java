module ap.ex2.bookscrabble {
    requires javafx.controls;
    requires javafx.fxml;
            
                            
    opens ap.ex2.bookscrabble to javafx.fxml;
    exports ap.ex2.bookscrabble;
}