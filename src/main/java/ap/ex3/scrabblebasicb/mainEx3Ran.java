package ap.ex3.scrabblebasicb;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class mainEx3Ran {
    private static String TARGET_URL = "http://localhost:8080/ScrabbleBasicB_war_exploded/api/hello-world";
    public static void main(String[] args) throws URISyntaxException, IOException, InterruptedException {

        URI targetURI = new URI(TARGET_URL);
        HttpRequest httpRequest = HttpRequest.newBuilder()
                .uri(targetURI)
                .GET()
                .build();

        HttpClient httpClient = HttpClient.newHttpClient();
        HttpResponse<String> response = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
        System.out.println(response.body());
    }
}
