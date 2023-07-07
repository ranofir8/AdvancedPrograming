package ap.ex3.GameScrabbleServer.rest;

import ap.ex3.GameScrabbleServer.Saves.GameSave;
import ap.ex3.GameScrabbleServer.ScrabbleGameServer;
import ap.ex3.GameScrabbleServer.db.GameNotFoundException;
import ap.ex3.GameScrabbleServer.db.InvalidHostException;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;


@Path ("/")
public class HttpRestManager {
    // todo - Moriya
    @GET
    @Path ("/loadGame")
    @Produces("text/plain")
    public Response getGame() {
        try {
            ScrabbleGameServer sgs = ScrabbleGameServer.getInstance();
            GameSave loadedGame = sgs.loadExistingGame(100, "Gilad");
        } catch (GameNotFoundException e) {
            return Response.status(400, "Game does not exist.").build();
        } catch (InvalidHostException e) {
            return Response.status(400, "You are not the host for this game.").build();
        }

        return Response.ok("Game loaded Successfully, here it is: ...").build();
    }
}
