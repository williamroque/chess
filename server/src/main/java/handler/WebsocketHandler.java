package handler;

import chess.ChessGame;
import chess.ChessMove;
import chess.ChessPiece;
import chess.InvalidMoveException;
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
            UserGameCommand.CommandType.CONNECT, this::connect,
            UserGameCommand.CommandType.MAKE_MOVE, this::makeMove,
            UserGameCommand.CommandType.RESIGN, this::resign,
            UserGameCommand.CommandType.LEAVE, this::leave
    );

    public WebsocketHandler(GameService gameService, UserService userService, ConnectionManager manager) {
        super(userService);
        this.gameService = gameService;

        this.manager = manager;
    }

    private void connect(UserGameCommand command, WsContext ctx) {
        String authToken = command.getAuthToken();
        int gameID = command.getGameID();

        try {
            if (!gameService.checkGame(authToken, gameID)) {
                throw new InvalidCredentialsException("");
            }

            Connection connection = manager.add(authToken, ctx, gameID);

            GameData game = gameService.getGame(authToken, gameID);
            String username = userService.getUsernameByToken(authToken);

            String userColor = null;

            if (game.whiteUsername() != null && game.whiteUsername().equals(username)) {
                userColor = "white";
            } else if (game.blackUsername() != null && game.blackUsername().equals(username)) {
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
            Connection connection = new Connection(authToken, ctx, gameID);
            connection.send(new ErrorMessage("Error: Invalid credentials or game ID"));
        }
    }

    private void makeMove(UserGameCommand command, WsContext ctx) {
        String authToken = command.getAuthToken();
        int gameID = command.getGameID();

        Connection connection = new Connection(
                authToken,
                ctx,
                gameID
        );

        try {
            if (!gameService.checkGame(authToken, gameID)) {
                throw new InvalidCredentialsException("Error: Invalid credentials or game ID");
            }

            String currentUsername = userService.getUsernameByToken(authToken);

            GameData data = gameService.getGame(authToken, gameID);

            ChessGame.TeamColor userColor = null;

            if (data.blackUsername() != null && data.blackUsername().equals(currentUsername)) {
                userColor = ChessGame.TeamColor.BLACK;
            } else if (data.whiteUsername() != null && data.whiteUsername().equals(currentUsername)) {
                userColor = ChessGame.TeamColor.WHITE;
            }

            if (userColor == null) {
                throw new InvalidMoveException("Error: Observers can't make moves");
            }

            ChessGame game = data.game();
            ChessMove move = command.getMove();

            ChessPiece startPiece = game.getBoard().getPiece(move.getStartPosition());

            if (startPiece != null && startPiece.getTeamColor() != userColor) {
                throw new InvalidMoveException("Error: You don't have a piece at that position");
            }

            game.makeMove(command.getMove());
            gameService.updateGame(authToken, data);

            manager.broadcast(gameID, new LoadGameMessage(data), null);
            manager.broadcast(gameID, new NotificationMessage(
                    currentUsername + " just moved"
            ), authToken);

            ChessGame.TeamColor winner = game.getWinner();

            if (winner == null) {
                return;
            }

            switch (winner) {
                case WHITE:
                    manager.broadcast(gameID, new NotificationMessage(
                            "The game has ended in checkmate. White wins!"
                    ), null);
                    break;
                case BLACK:
                    manager.broadcast(gameID, new NotificationMessage(
                            "The game has ended in checkmate. Black wins!"
                    ), null);
                    break;
            }
        } catch (DataAccessException | InvalidMoveException e) {
            connection.send(new ErrorMessage(e.getMessage()));
        }
    }

    private void resign(UserGameCommand command, WsContext ctx) {
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

            String currentUsername = userService.getUsernameByToken(authToken);

            GameData data = gameService.getGame(authToken, gameID);
            ChessGame game = data.game();

            ChessGame.TeamColor userColor = null;

            if (data.blackUsername() != null && data.blackUsername().equals(currentUsername)) {
                userColor = ChessGame.TeamColor.BLACK;
            } else if (data.whiteUsername() != null && data.whiteUsername().equals(currentUsername)) {
                userColor = ChessGame.TeamColor.WHITE;
            }

            if (userColor == null) {
                connection.send(new ErrorMessage("Error: Non-player user cannot resign"));
                return;
            }

            if (game.isGameOver()) {
                connection.send(new ErrorMessage("Error: The game is already over"));
                return;
            }

            game.resign(userColor);
            gameService.updateGame(authToken, data);

            manager.broadcast(gameID, new NotificationMessage(
                    (userColor == ChessGame.TeamColor.BLACK ? "Black" : "White") + " team has resigned"
            ), null);
        } catch (DataAccessException e) {
            connection.send(new ErrorMessage("Error: Invalid credentials or game ID"));
        }
    }

    private void leave(UserGameCommand command, WsContext ctx) {
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

            String currentUsername = userService.getUsernameByToken(authToken);

            GameData data = gameService.getGame(authToken, gameID);

            if (data.blackUsername() != null && data.blackUsername().equals(currentUsername)) {
                GameData updatedData = new GameData(
                        gameID,
                        data.whiteUsername(),
                        null,
                        data.gameName(),
                        data.game()
                );
                gameService.updateGame(authToken, updatedData);
            } else if (data.whiteUsername() != null && data.whiteUsername().equals(currentUsername)) {
                GameData updatedData = new GameData(
                        gameID,
                        null,
                        data.blackUsername(),
                        data.gameName(),
                        data.game()
                );
                gameService.updateGame(authToken, updatedData);
            }

            manager.remove(authToken);
            manager.broadcast(gameID, new NotificationMessage(
                    currentUsername + " has left the game"
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
