package ap.ex3.GameScrabbleServer.rest;

import ap.ex3.GameScrabbleServer.Saves.GameSave;
import ap.ex3.GameScrabbleServer.ScrabbleGameServer;
import ap.ex3.GameScrabbleServer.db.GameNotFoundException;
import ap.ex3.GameScrabbleServer.db.InvalidHostException;

import javax.ws.rs.*;
import javax.ws.rs.core.Response;


@Path ("/")
public class HttpRestManager {
    // todo - Moriya
    @GET
    @Path ("/loadGame")
    @Produces("text/plain")
    public Response getGame(@QueryParam("ID") String ID, @QueryParam("HostName") String HostName) {
        try {
            ScrabbleGameServer sgs = ScrabbleGameServer.getInstance();
            GameSave loadedGame = sgs.loadExistingGame(Integer.parseInt(ID), HostName);
            return Response.ok(loadedGame.convertToJSON()).build();
        } catch (GameNotFoundException e) {
            return Response.status(400, "Game does not exist.").build();
        } catch (InvalidHostException e) {
            return Response.status(400, "You are not the host for this game.").build();
        }

    }

//    @POST
//    @Path ("/loadGame")
//    @Produces("text/plain")
//    @Consumes("application/json")
//    public Response PostGame(GameSave gameData) {
//        try {
//            ScrabbleGameServer sgs = ScrabbleGameServer.getInstance();
//            //calc id
//            //send to save in database
//            //return id
//        } catch (GameNotFoundException e) {
//            return Response.status(400, ).build();
//
//        return Response.ok(id.toString()).build();
//    }
}
