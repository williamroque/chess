package handler;

import dataaccess.exceptions.AuthTokenNotFoundException;
import io.javalin.http.Context;
import model.GameData;
import server.InvalidRequestException;
import service.GameService;
import service.UserService;

import java.util.Collection;
import java.util.Map;

public class GameHandler extends Handler {
    GameService gameService;

    public GameHandler(GameService gameService, UserService userService) {
        super(userService);
        this.gameService = gameService;
    }

    public void listGames(Context ctx) {
        String authToken = ctx.header("authorization");

        try {
            Collection<GameData> games = gameService.listGames(authToken);
            ctx.status(200);
            ctx.json(Map.of("games", games));
        } catch(AuthTokenNotFoundException _) {
            ctx.status(401);
            ctx.json("{ \"message\": \"Error: unauthorized\" }");
        } catch (Exception e) {
            System.err.println(e);
            ctx.status(500);
            ctx.json("{ \"message\": \"Error: server error\" }");
        }
    }

    public void createGame(Context ctx) {
        String authToken = ctx.header("authorization");
        String gameName;

        try {
            GameData gameData = getBodyObject(ctx, GameData.class);

            if (gameData.gameName() == null || gameData.gameName().isBlank()) {
                throw new InvalidRequestException("Invalid game name.");
            }

            gameName = gameData.gameName();
        } catch (InvalidRequestException _) {
            ctx.status(400);
            ctx.json("{ \"message\": \"Error: bad request\" }");

            return;
        }

        try {
            GameData newGameData = gameService.createGame(authToken, gameName);
            ctx.status(200);
            ctx.json(Map.of("gameID", newGameData.gameID()));
        } catch(AuthTokenNotFoundException _) {
            ctx.status(401);
            ctx.json("{ \"message\": \"Error: unauthorized\" }");
        } catch (Exception e) {
            System.err.println(e);
            ctx.status(500);
            ctx.json("{ \"message\": \"Error: server error\" }");
        }
    }
}
