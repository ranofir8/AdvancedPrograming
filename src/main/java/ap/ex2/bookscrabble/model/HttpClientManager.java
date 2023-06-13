package ap.ex2.bookscrabble.model;

import org.apache.http.client.HttpClient;
import org.apache.http.HttpRequest;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;


import java.net.URI;

import org.apache.http.HttpRequest.*;
//import java.net.HttpRequest;


//import org.apache.http.impl.client.CloseableHttpClient;
//import org.apache.http.impl.client.HttpClients;



public class HttpClientManager {

    private HttpClient http_client;
    private  HttpGet getRequest;

    public HttpClientManager(String server_url) {

        http_client = new DefaultHttpClient();
        getRequest = new HttpGet(server_url);

    }


    public boolean sendGame(String json){
//        getRequest.
        return true;
    }

    public void close(){
    }
}
