package com.example.demo;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import scrabble.Board;
import scrabble.Tile;

import java.net.URL;
import java.util.Random;
import java.util.ResourceBundle;

public class MyBoard implements Initializable {
    @FXML
    Canvas myCanvas;
    Board board;

    public MyBoard() {
        this.board = new Board();
    }

    public void clearCanvas(){
        GraphicsContext gc=myCanvas.getGraphicsContext2D();
        gc.clearRect(0,0,myCanvas.getWidth(),myCanvas.getHeight());
    }

    public void drawRand(){
        Random r=new Random();
        GraphicsContext gc=myCanvas.getGraphicsContext2D();
        int x=r.nextInt((int)myCanvas.getWidth());
        int y=r.nextInt((int)myCanvas.getHeight());
        int w=r.nextInt((int)myCanvas.getWidth() - x);
        int h=r.nextInt((int)myCanvas.getHeight() - y);
        gc.strokeRect(x,y,w,h);
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        //
    }

    public void drawBoardTest() {
        GraphicsContext gc = this.myCanvas.getGraphicsContext2D();
//        Board b = this.myPlayer.getGameBoard();  todo get from model
        Board b = this.board;
        int w = (int) this.myCanvas.getWidth(), h = (int) this.myCanvas.getHeight();
        int square = (int)(Math.min(w, h) / (float)Math.max(Board.ROW_NUM, Board.COL_NUM));
        //fill board with words for test:
        for (int row = 0; row < Board.ROW_NUM; row++) {
            for (int col = 0; col < Board.COL_NUM; col++) {
                int m = b.getMultiplierAtInt(row, col);
                Color toFill;
                switch (m) {
                    case 11:
                        toFill = Color.color(0, 153.0/255, 0);
                        break;
                    case 13:
                        toFill = Color.color(0, 153.0/255, 1);
                        break;
                    case 12:
                        toFill = Color.color(100.0/255, 204.0/255, 1);
                        break;
                    case 21:
                        toFill = Color.color(1, 1, 153.0/255);
                        break;
                    case 31:
                        toFill = Color.color(1, 51.0/255, 51.0/255);
                        break;
                    default:
                        toFill = Color.GRAY;
                        break;
                }
                gc.setFill(toFill);
                gc.fillRect(row * square, col * square, square, square);
                gc.strokeRect(row * square, col * square, square, square);
                Tile t = b.getTileAt(row,col);
                if(t != null) {
                    gc.fillText(""+t.letter,col*square+10,row*square-10);
                }
            }
        }

    }
}

// fx:controller="com.example.demo.MyBoard"
//C:\Users\אופיר\OneDrive - Bar-Ilan University\Desktop\AdvancedPrograming\Temp\out\artifacts\demo_jar
