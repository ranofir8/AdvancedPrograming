package ap.ex2.bookscrabble.model.host;

import ap.ex3.GameScrabbleServer.Saves.GameSave;
import com.google.gson.Gson;
//import org.apache.http.HttpResponse;
//import org.apache.http.client.HttpClient;
//import org.apache.http.HttpRequest;
//import org.apache.http.client.methods.HttpGet;
//import org.apache.http.client.methods.HttpPost;
//import org.apache.http.client.methods.HttpGet;
//import org.apache.http.impl.client.DefaultHttpClient;


//import java.io.BufferedReader;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
//import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import javax.ws.rs.core.UriBuilder;

import static java.lang.Integer.parseInt;

//import java.net.HttpRequest;


//import org.apache.http.impl.client.CloseableHttpClient;
//import org.apache.http.impl.client.HttpClients;


public class HttpClientManager {
    public static GameSave httpGet(String server_url , int gameId , String HostName) throws IOException, InterruptedException, URISyntaxException {

        UriBuilder uriBuilder = UriBuilder.fromUri(server_url + "/loadGame/");
        uriBuilder.queryParam("ID", gameId);
        uriBuilder.queryParam("HostName", HostName);

        HttpRequest httpRequest = HttpRequest.newBuilder()
                .uri(uriBuilder.build())
                .GET()
                .build();

        HttpClient httpClient = HttpClient.newHttpClient();
        HttpResponse<String> response = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());

        return GameSave.convertFromJSON(response.body());

    }

    public static int httpPost(String server_url, GameSave gameToSave) throws IOException, URISyntaxException, InterruptedException {
        URI req_uri = new URI(server_url + "/restoreGame");
        HttpRequest httpRequest = HttpRequest.newBuilder()
                .uri(req_uri)
                .POST(HttpRequest.BodyPublishers.ofString(gameToSave.convertToJSON()))
                .build();
        HttpClient httpClient = HttpClient.newHttpClient();
        HttpResponse<String> response = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
        return parseInt(response.body());
    }
}
