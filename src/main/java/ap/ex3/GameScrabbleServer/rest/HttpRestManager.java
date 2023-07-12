package ap.ex3.GameScrabbleServer.rest;

import ap.ex3.GameScrabbleServer.Saves.GameSave;
import ap.ex3.GameScrabbleServer.ScrabbleGameServer;
import ap.ex3.GameScrabbleServer.db.GameNotFoundException;
import ap.ex3.GameScrabbleServer.db.InvalidHostException;

import javax.ws.rs.*;
import javax.ws.rs.core.Response;


@Path ("/")
public class HttpRestManager {
    @GET
    @Path ("/loadGame")
    @Produces("text/plain")
    public Response getGame(@QueryParam("ID") String strID, @QueryParam("HostName") String hostName) {
        try {
            if (strID == null || hostName == null)
                return Response.status(400, "Please specify ID, HostName parameters to GET.").build();

            try {
                int intID = Integer.parseInt(strID);
                ScrabbleGameServer sgs = ScrabbleGameServer.getInstance();
                GameSave loadedGame = sgs.loadExistingGame(intID, hostName);
                return Response.ok(loadedGame.convertToJSON()).build();
            } catch (NumberFormatException e) {
                return Response.status(400, "Game ID must be an integer.").build();
            }

        } catch (GameNotFoundException e) {
            return Response.status(400, "Game does not exist.").build();
        } catch (InvalidHostException e) {
            return Response.status(400, "You are not the host for this game, therefore you cannot access it.").build();
        }

    }

    @GET
    @Path ("/androidLoadScore")
    @Produces("text/plain")
    public Response getScore(@QueryParam("ID") String ID, @QueryParam("HostName") String HostName) {
        try {
            ScrabbleGameServer sgs = ScrabbleGameServer.getInstance();
            GameSave loadedGame = sgs.loadExistingGame(Integer.parseInt(ID), HostName);
            return Response.ok(loadedGame.getListOfPlayers().toString()).build(); //TODO: send score list
        } catch (GameNotFoundException e) {
            return Response.status(400, "Game does not exist.").build();
        } catch (InvalidHostException e) {
            return Response.status(400, "You are not the host for this game.").build();
        }

    }

    @POST
    @Path ("/saveGame")
    @Produces("text/plain")
    public Response PostGame(String GameData) {
        ScrabbleGameServer sgs = ScrabbleGameServer.getInstance();
        int gameID = sgs.saveNewGame(GameSave.convertFromJSON(GameData));
        return Response.ok(String.valueOf(gameID)).build();
    }
}
