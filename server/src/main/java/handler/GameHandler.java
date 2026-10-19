package handler;

import chess.ChessGame;
import dataaccess.exceptions.AlreadyTakenException;
import dataaccess.exceptions.AuthTokenNotFoundException;
import io.javalin.http.Context;
import model.GameData;
import server.InvalidRequestException;
import service.GameService;
import service.UserService;

import java.util.Collection;
import java.util.Map;

record GameRequest(
        int gameID,
        String gameName,
        String game,
        String playerColor
) { }

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
            GameRequest request = getBodyObject(ctx, GameRequest.class);

            if (request.gameName() == null || request.gameName().isBlank()) {
                throw new InvalidRequestException("Invalid game name.");
            }

            gameName = request.gameName();
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

    public void joinGame(Context ctx) {
        String authToken = ctx.header("authorization");
        GameRequest gameRequest;

        try {
            gameRequest = getBodyObject(ctx, GameRequest.class);

            if (gameRequest.playerColor() == null
                    || !gameRequest.playerColor().equals("BLACK") && !gameRequest.playerColor().equals("WHITE") ) {
                throw new InvalidRequestException("Invalid player color.");
            }

            if (gameRequest.gameID() <= 0) {
                throw new InvalidRequestException("Invalid game ID.");
            }
        } catch (InvalidRequestException _) {
            ctx.status(400);
            ctx.json("{ \"message\": \"Error: bad request\" }");

            return;
        }

        try {
            gameService.joinGame(
                    authToken,
                    gameRequest.gameID(),
                    gameRequest.playerColor().equals("BLACK") ? ChessGame.TeamColor.BLACK : ChessGame.TeamColor.WHITE
            );
            ctx.status(200);
            ctx.json("{}");
        } catch(AuthTokenNotFoundException _) {
            ctx.status(401);
            ctx.json("{ \"message\": \"Error: unauthorized\" }");
        } catch(AlreadyTakenException _) {
            ctx.status(403);
            ctx.json("{ \"message\": \"Error: already taken\" }");
        } catch (Exception e) {
            System.err.println(e);
            ctx.status(500);
            ctx.json("{ \"message\": \"Error: server error\" }");
        }
    }
}
