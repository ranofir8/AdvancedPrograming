package ap.ex3.GameScrabbleServer.rest;

import ap.ex3.GameScrabbleServer.Saves.GameSave;
import ap.ex3.GameScrabbleServer.Saves.PlayerSave;
import ap.ex3.GameScrabbleServer.ScrabbleGameServer;
import ap.ex3.GameScrabbleServer.db.GameNotFoundException;
import ap.ex3.GameScrabbleServer.db.InvalidHostException;
import com.google.gson.Gson;

import javax.ws.rs.*;
import javax.ws.rs.core.Response;
import java.util.List;


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
    @Path ("/android/loadScore")
    @Produces("text/plain")
    public Response getScore(@QueryParam("ID") String strID) {
        try {
            if (strID == null)
                return Response.status(400, "Please specify ID parameter to GET.").build();

            try {
                int intID = Integer.parseInt(strID);
                ScrabbleGameServer sgs = ScrabbleGameServer.getInstance();
                List<PlayerSave> loadedGameScores = sgs.loadScoresOfGame(intID);
                return Response.ok(convertToJSON(loadedGameScores)).build();
            } catch (NumberFormatException e) {
                return Response.status(400, "Game ID must be an integer.").build();
            }

        } catch (GameNotFoundException e) {
            return Response.status(400, "Game does not exist.").build();
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

    private static <T> String convertToJSON(List<T> list) {
        Gson gson = new Gson();
        return gson.toJson(list);
    }
}
