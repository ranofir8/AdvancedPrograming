package ap.ex2.bookscrabble.model;

import com.google.gson.Gson;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.HttpRequest;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;


import java.io.BufferedReader;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

import org.apache.http.HttpRequest.*;
//import java.net.HttpRequest;


//import org.apache.http.impl.client.CloseableHttpClient;
//import org.apache.http.impl.client.HttpClients;



public class HttpClientManager {

    public static String httpGet(String server_url , String param) throws IOException, URISyntaxException {

        String req_url = server_url + "/" + param;
        HttpClient client = new DefaultHttpClient();
        String encodedUrl = URLEncoder.encode(req_url, StandardCharsets.UTF_8.toString());
        HttpGet request = new HttpGet(new URI(encodedUrl));
        HttpResponse response = client.execute(request);

        // Get the response
        Scanner sc = new Scanner(response.getEntity().getContent());

        String res = "";
        while (sc.hasNext()) {
            res += sc.nextLine();
        }
        return res;

    }

    public static String httpPost(String server_url, String param) throws IOException, URISyntaxException {


        String req_url = server_url + "/" + param;
        HttpClient client = new DefaultHttpClient();
        String encodedUrl = URLEncoder.encode(server_url, StandardCharsets.UTF_8.toString());
        HttpPost request = new HttpPost(new URI(encodedUrl));
        HttpResponse response = client.execute(request);

        // Get the response
        Scanner sc = new Scanner(response.getEntity().getContent());

        String res = "";
        while (sc.hasNext()) {
            res += sc.nextLine();
        }
        return res;

    }
}
