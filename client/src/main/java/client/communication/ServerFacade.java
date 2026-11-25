package client.communication;

import client.exceptions.FacadeException;
import com.google.gson.Gson;
import jakarta.websocket.DeploymentException;
import model.AuthData;
import model.GameData;
import model.GameDataList;
import model.UserData;
import websocket.commands.UserGameCommand;

import java.io.IOException;
import java.util.Map;

public class ServerFacade {
    private final HttpCommunicator httpCommunicator;
    private final WebsocketCommunicator websocketCommunicator;

    public void clear() throws FacadeException {
        httpCommunicator.sendRequest(
                "DELETE",
                "/db",
                null,
                null,
                null,
                Map.of(
                        500, "There was an internal server error while trying to clear the database."
                )
        );
    }

    public AuthData register(UserData data) throws FacadeException {
        return httpCommunicator.sendRequest(
                "POST",
                "/user",
                new Gson().toJson(data),
                null,
                AuthData.class,
                Map.of(
                        400, "Invalid registration details.",
                        403, "This username is already taken!",
                        500, "There was an internal server error while trying to register."
                )
        );
    }

    public AuthData login(UserData data) throws FacadeException {
        return httpCommunicator.sendRequest(
                "POST",
                "/session",
                new Gson().toJson(data),
                null,
                AuthData.class,
                Map.of(
                        400, "Invalid credentials format.",
                        401, "Invalid login credentials.",
                        500, "There was an internal server error while trying to log in."
                )
        );
    }

    public void logout(AuthData data) throws FacadeException {
        String[] headers = {
                "authorization", data.authToken()
        };

        httpCommunicator.sendRequest(
                "DELETE",
                "/session",
                null,
                headers,
                AuthData.class,
                Map.of(
                        401, "Invalid authentication token.",
                        500, "There was an internal server error while trying to log out."
                )
        );
    }

    public GameDataList listGames(AuthData data) throws FacadeException {
        String[] headers = {
                "authorization", data.authToken()
        };

        return httpCommunicator.sendRequest(
                "GET",
                "/game",
                null,
                headers,
                GameDataList.class,
                Map.of(
                        401, "Invalid authentication token.",
                        500, "There was an internal server error while trying to get game list."
                )
        );
    }

    public GameData createGame(AuthData data, String gameName) throws FacadeException {
        String[] headers = {
                "authorization", data.authToken()
        };

        return httpCommunicator.sendRequest(
                "POST",
                "/game",
                new Gson().toJson(Map.of(
                        "gameName", gameName
                )),
                headers,
                GameData.class,
                Map.of(
                        400, "Invalid game details.",
                        401, "Invalid authentication token.",
                        500, "There was an internal server error while trying to get game list."
                )
        );
    }

    public GameData joinGame(AuthData data, String playerColor, int gameID) throws FacadeException {
        String[] headers = {
                "authorization", data.authToken()
        };

        GameData game = httpCommunicator.sendRequest(
                "PUT",
                "/game",
                new Gson().toJson(Map.of(
                        "playerColor", playerColor,
                        "gameID", gameID
                )),
                headers,
                GameData.class,
                Map.of(
                        400, "Invalid game details.",
                        401, "Invalid authentication token.",
                        403, "There is already someone playing under that color!",
                        500, "There was an internal server error while trying to get game list."
                )
        );

        return game;
    }

    public void connectWS(String authToken, int gameID) throws DeploymentException, IOException {
        websocketCommunicator.connect();
        websocketCommunicator.send(new UserGameCommand(
                UserGameCommand.CommandType.CONNECT,
                authToken,
                gameID,
                null));
    }

    public ServerFacade(HttpCommunicator httpCommunicator, WebsocketCommunicator websocketCommunicator) {
        this.httpCommunicator = httpCommunicator;
        this.websocketCommunicator = websocketCommunicator;
    }
}
