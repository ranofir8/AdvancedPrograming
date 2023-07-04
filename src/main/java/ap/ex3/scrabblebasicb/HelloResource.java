package ap.ex3.scrabblebasicb;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import java.util.HashMap;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

@Path("/casual-table")
public class HelloResource {

    @GET
    @Produces("text/plain")
    public String hello() {
        HashMap<String, Integer> playerScore = new HashMap<>();
        playerScore.put("Player1", 100);
        playerScore.put("Player2", 200);
        playerScore.put("Player3", 150);
        //String res = new GsonBuilder().create().toJson(playerScore);
        String res = formatHashMapToJson(playerScore);
        return res;
    }

    private static String formatHashMapToJson(HashMap<String, Integer> hashMap) {
        StringBuilder jsonBuilder = new StringBuilder("{");

        // Iterate over the HashMap entries and construct the JSON string
        for (Map.Entry<String, Integer> entry : hashMap.entrySet()) {
            String key = entry.getKey();
            Integer value = entry.getValue();

            // Append key-value pair to the JSON string
            jsonBuilder.append("\"").append(key).append("\":").append(value).append(",");
        }

        // Remove the trailing comma if present
        if (jsonBuilder.length() > 1) {
            jsonBuilder.deleteCharAt(jsonBuilder.length() - 1);
        }

        // Close the JSON object
        jsonBuilder.append("}");

        return jsonBuilder.toString();
    }
}