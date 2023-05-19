package ap.ex2.bookscrabble.view;

import ap.ex2.scrabble.Board;
import ap.ex2.scrabble.Tile;
import ap.ex2.scrabble.Word;

public class test_ControllerGameView {
    static Tile.Bag b;
    public test_ControllerGameView() {
        b= new Tile.Bag();

    }



        private Tile[] get(String s) {
            Tile[] ts=new Tile[s.length()];
            int i=0;
            for(char c: s.toCharArray()) {
                ts[i]=b.getTile(c);
                i++;
            }
            return ts;
        }


        public void testBoard(Board b) {

            Tile[] ts=new Tile[10];

            Word horn=new Word(get("HORN"), 7, 5, false);
            if(b.tryPlaceWord(horn)!=14)
                System.out.println("problem in placeWord for 1st word (-10)");

            Word farm=new Word(get("FA_M"), 5, 7, true);
            if(b.tryPlaceWord(farm)!=9)
                System.out.println("problem in placeWord for 2ed word (-10)");
//
//            Word paste=new Word(get("PASTE"), 9, 5, false);
//            if(b.tryPlaceWord(paste)!=25)
//                System.out.println("problem in placeWord for 3ed word (-10)");
//
//            Word mob=new Word(get("_OB"), 8, 7, false);
//            if(b.tryPlaceWord(mob)!=18)
//                System.out.println("problem in placeWord for 4th word (-15)");
//
//            Word bit=new Word(get("BIT"), 10, 4, false);
//            if(b.tryPlaceWord(bit)!=22)
//                System.out.println("problem in placeWord for 5th word (-15)");


        }

//        public static void main(String[] args) {
//            testBag(); // 30 points
//            testBoard(); // 70 points
//            System.out.println("done");
//        }



}
