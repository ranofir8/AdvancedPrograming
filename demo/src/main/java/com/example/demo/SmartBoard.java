package com.example.demo;
// import com.example.demo.MyBoard;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.layout.AnchorPane;
import scrabble.Board;

import java.io.IOException;
import java.net.URL;

public class SmartBoard extends AnchorPane {
    MyBoard board;
    public SmartBoard(){
        super();
        try {
            board = new MyBoard();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("MyBoard.fxml"));
            loader.setController(board);
            Node b = loader.load();
            this.getChildren().add(b);
        }catch (Exception e){}

    }
}

//<?import com.example.demo.SmartBoard?>