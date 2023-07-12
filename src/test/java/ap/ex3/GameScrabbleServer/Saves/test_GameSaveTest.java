package ap.ex3.GameScrabbleServer.Saves;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class test_GameSaveTest {

    @Test
    void convertToJSON() {
        GameSave gs = test_GameSave.createDummyObject();
        System.out.println(gs.convertToJSON());
    }

    @Test
    void convertFromJSON() {
        String sampleString = "{\"gameID\":123,\"hostName\":\"hostGilad\",\"gameBoardString\":\"ALOF     SADE                                                                                                                                                                                                                    \",\"listOfPlayers\":[{\"playerName\":\"Gilad\",\"playerScore\":90,\"playerTiles\":\"FORANGE\"},{\"playerName\":\"Mor\",\"playerScore\":-80,\"playerTiles\":\"CYBERRR\"},{\"playerName\":\"Ofir\",\"playerScore\":100,\"playerTiles\":\"HELSLKOTILE\"}]}\n";
        GameSave gs = GameSave.convertFromJSON(sampleString);
    }
}