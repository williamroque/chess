package handler;

import chess.ChessGame;
import com.google.gson.Gson;
import dataaccess.exceptions.DataAccessException;
import dataaccess.exceptions.InvalidCredentialsException;
import io.javalin.websocket.WsContext;
import io.javalin.websocket.WsMessageContext;
import model.GameData;
import server.Connection;
import server.ConnectionManager;
import service.GameService;
import service.UserService;
import websocket.commands.UserGameCommand;
import websocket.messages.ErrorMessage;
import websocket.messages.LoadGameMessage;
import websocket.messages.NotificationMessage;

import java.util.Map;
import java.util.function.BiConsumer;

public class WebsocketHandler extends Handler {
    private final GameService gameService;
    private final ConnectionManager manager;

    Map<UserGameCommand.CommandType, BiConsumer<UserGameCommand, WsContext>> COMMANDS = Map.of(
            UserGameCommand.CommandType.CONNECT, this::connect
    );

    public WebsocketHandler(GameService gameService, UserService userService, ConnectionManager manager) {
        super(userService);
        this.gameService = gameService;

        this.manager = manager;
    }

    private void connect(UserGameCommand command, WsContext ctx) {
        String authToken = command.getAuthToken();
        int gameID = command.getGameID();

        Connection connection = new Connection(
                authToken,
                ctx,
                gameID
        );

        try {
            if (!gameService.checkGame(authToken, gameID)) {
                throw new InvalidCredentialsException("");
            }

            manager.add(authToken, ctx, gameID);

            GameData game = gameService.getGame(authToken, gameID);
            String username = userService.getUsernameByToken(authToken);

            String userColor = null;

            if (game.whiteUsername().equals(username)) {
                userColor = "white";
            } else if (game.blackUsername().equals(username)) {
                userColor = "black";
            }

            connection.send(new LoadGameMessage(game));
            manager.broadcast(gameID, new NotificationMessage(
                    username + " joined as " + (userColor == null
                            ? "an observer"
                            : "a player on the " + userColor + " team"
                    )
            ), authToken);
        } catch (DataAccessException e) {
            connection.send(new ErrorMessage("Error: Invalid credentials or game ID"));
        }
    }

    public void onConnect(WsContext ctx) {
        ctx.enableAutomaticPings();
    }

    public void onClose(WsContext ctx) {
    }

    public void onMessage(WsMessageContext ctx) {
        UserGameCommand command = new Gson().fromJson(ctx.message(), UserGameCommand.class);

        COMMANDS.get(command.getCommandType())
                .accept(command, ctx);
    }

    public void onError(WsContext ctx) {
    }
}
