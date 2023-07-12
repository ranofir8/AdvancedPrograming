package ap.ex3.GameScrabbleServer.rest;

import ap.ex3.GameScrabbleServer.Saves.GameSave;
import ap.ex3.GameScrabbleServer.db.DBServerException;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import javax.ws.rs.core.UriBuilder;

import static java.lang.Integer.parseInt;

public class HttpClientManager {
    static final String LOAD_GAME_URI = "scrabble/loadGame";
    static final String SAVE_GAME_URI = "scrabble/saveGame";
    private final URI baseUri;

    public HttpClientManager(String baseUrl) throws URISyntaxException {
        this.baseUri = URI.create(baseUrl + "/");
    }

    public GameSave httpGet(int gameId , String HostName) throws IOException, DBServerException, GameServer400 {
        URI u = this.baseUri.resolve("./" + LOAD_GAME_URI);

        UriBuilder uriBuilder = UriBuilder.fromUri(u);
        uriBuilder.queryParam("ID", gameId);
        uriBuilder.queryParam("HostName", HostName);

        HttpRequest httpRequest = HttpRequest.newBuilder()
                .uri(uriBuilder.build())
                .GET()
                .build();

        HttpClient httpClient = HttpClient.newHttpClient();
        HttpResponse<String> response = null;
        try {
            response = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
        } catch (InterruptedException e) {
            throw new IOException();
        }

        switch (response.statusCode()) {
            case 200:   // ok
                return GameSave.convertFromJSON(response.body());
            case 400:   // logical error in the request
                this.throw400error(response);
            case 500:  // internal server error
                throw new DBServerException();
            default:
                System.out.println("Unhandled HTTP response: " + response.statusCode() + "\n\n" + response.body());
        }

        return null;
    }

    private void throw400error(HttpResponse<String> response) throws GameServer400 {
        String[] errorBody = response.body().split("<p><b>Message</b> ");
        if (errorBody.length < 2)
            throw new GameServer400("Error in 400 parsing");
        String[] errorMessage = errorBody[1].split("</p><p>");
        if (errorMessage.length < 2)
            throw new GameServer400("Error in 400 parsing");
        throw new GameServer400(errorMessage[0]);
    }

    public int httpPost(GameSave gameToSave) throws IOException, DBServerException, GameServer400 {
        UriBuilder uriBuilder = UriBuilder.fromUri(this.baseUri);
        URI u = uriBuilder.build().resolve("./" + SAVE_GAME_URI);

        HttpRequest httpRequest = HttpRequest.newBuilder()
                .uri(u)
                .POST(HttpRequest.BodyPublishers.ofString(gameToSave.convertToJSON()))
                .build();
        HttpClient httpClient = HttpClient.newHttpClient();
        HttpResponse<String> response = null;
        try {
            response = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
        } catch (InterruptedException e) {
            throw new IOException();
        }

        switch (response.statusCode()) {
            case 200:
                return parseInt(response.body());
            case 400:
                this.throw400error(response);
            case 500:  // internal server error
                throw new DBServerException();
            default:
                System.out.println("Unhandled HTTP response: " + response.statusCode() + "\n\n" + response.body());
        }
        return -1;
    }
}
