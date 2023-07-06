package ap.ex3.GameScrabbleServer;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

@Path ("/GameServer")
public class HTTPServerManager {
    // todo - Moriya
    @GET
    @Produces("text/plain")
    public String getGame() {
        return "Game Saved Successfully";
    }
}
