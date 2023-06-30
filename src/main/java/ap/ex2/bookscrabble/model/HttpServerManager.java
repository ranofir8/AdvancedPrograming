package ap.ex2.bookscrabble.model;

import javax.ws.rs.GET;
import javax.ws.rs.Produces;
import javax.ws.rs.Path;

@Path ("/GameServer")
public class HttpServerManager {

    @GET
    @Produces("text/plain")
    public String getGame() {
        return "Game Saved Successfully";
    }

}
