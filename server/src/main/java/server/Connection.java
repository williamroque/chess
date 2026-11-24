package server;

import com.google.gson.Gson;
import io.javalin.websocket.WsContext;
import websocket.messages.ServerMessage;

public class Connection {
    private String authToken;
    private WsContext ctx;
    private int gameID;

    public Connection(String authToken, WsContext ctx, int gameID) {
        this.authToken = authToken;
        this.ctx = ctx;
        this.gameID = gameID;
    }

    public String getAuthToken() {
        return authToken;
    }

    public int getGameID() {
        return gameID;
    }

    public boolean isOpen() {
        return ctx.session.isOpen();
    }

    public void send(ServerMessage message) {
        ctx.send(new Gson().toJson(message));
    }
}
