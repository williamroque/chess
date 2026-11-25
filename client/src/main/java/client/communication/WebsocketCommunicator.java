package client.communication;

import client.ServerMessageObserver;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import jakarta.websocket.*;
import websocket.commands.UserGameCommand;
import websocket.messages.ServerMessage;
import websocket.messages.ServerMessageDeserializer;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

@ClientEndpoint
public class WebsocketCommunicator {
    private final URI uri;
    private final ServerMessageObserver observer;

    private Session session;

    public WebsocketCommunicator(String baseURL, ServerMessageObserver observer) throws URISyntaxException {
        uri = new URI(baseURL);
        this.observer = observer;
    }

    public void connect() throws DeploymentException, IOException {
        WebSocketContainer container = ContainerProvider.getWebSocketContainer();
        session = container.connectToServer(this, uri);
    }

    @OnOpen
    public void onOpen(Session session) {
        this.session = session;
    }

    @OnMessage
    public void onMessage(String message) {
        GsonBuilder builder = new GsonBuilder();
        builder.registerTypeAdapter(ServerMessage.class, new ServerMessageDeserializer());
        Gson gson = builder.create();

        ServerMessage parsedMessage = gson.fromJson(message, ServerMessage.class);
        observer.notify(parsedMessage);
    }

    public void send(UserGameCommand message) throws IOException {
        String serializedMessage = new Gson().toJson(message);
        this.session.getBasicRemote().sendText(serializedMessage);
    }
}
