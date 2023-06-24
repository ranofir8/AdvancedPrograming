package ap.ex2.bookscrabble.model;

import ap.ex2.bookscrabble.model.host.HostServer;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Set;

public class test_HostServer {

    @Test
    public void testGetOnlinePlayers() {
        HostServer hostServer = new HostServer(1234, 5, null);
        hostServer.setMyNickname("Player1");
        hostServer.setMyNickname("Player2");

        Set<String> onlinePlayers = hostServer.getOnlinePlayers();

        Assertions.assertEquals(2, onlinePlayers.size());
        Assertions.assertTrue(onlinePlayers.contains("Player1"));
        Assertions.assertTrue(onlinePlayers.contains("Player2"));
    }

    @Test
    public void testHasPlayerNamed() {
        HostServer hostServer = new HostServer(1234, 5, null);
        hostServer.setMyNickname("Player1");
        hostServer.setMyNickname("Player2");

        Assertions.assertTrue(hostServer.hasPlayerNamed("Player1"));
        Assertions.assertTrue(hostServer.hasPlayerNamed("Player2"));
        Assertions.assertFalse(hostServer.hasPlayerNamed("Player3"));
    }
}
