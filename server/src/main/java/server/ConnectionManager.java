package server;

import com.google.gson.Gson;
import io.javalin.websocket.WsContext;
import websocket.messages.ServerMessage;

import java.util.HashMap;
import java.util.Map;

public class ConnectionManager {
    Map<String, Connection> connections = new HashMap<>();

    public Connection add(String authToken, WsContext ctx, int gameID) {
        Connection connection = new Connection(authToken, ctx, gameID);

        connections.put(authToken, connection);

        return connection;
    }

    public Connection get(String authToken) {
        return connections.get(authToken);
    }

    public void remove(String authToken) {
        connections.remove(authToken);
    }

    public void broadcast(int gameID, ServerMessage notification, String excludeAuthToken) {
        for (Connection conn : connections.values()) {
            if (conn.isOpen() && conn.getGameID() == gameID) {
                if (!conn.getAuthToken().equals(excludeAuthToken)) {
                    conn.send(notification);
                }
            }
        }
    }
}
