package ap.ex2.bookscrabble.model;

import org.apache.http.client.methods.HttpGet;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URISyntaxException;

import static org.junit.jupiter.api.Assertions.*;

class HttpClientManagerTest {

    @BeforeEach
    void setUp() {
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void test_HttpGet() throws IOException, URISyntaxException {

        String res =  HttpClientManager.httpGet("http://localhost:8080/demo2_war_exploded/api","hello-world");
//        String res =  HttpClientManager.httpGet("http://localhost:8080/demo2_war_exploded/api/hello-world");

        Assertions.assertEquals("Hello, World!",res );

    }
}