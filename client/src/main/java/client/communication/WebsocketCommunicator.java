package client.communication;

import client.ServerMessageObserver;
import com.google.gson.Gson;
import jakarta.websocket.*;
import websocket.commands.UserGameCommand;
import websocket.messages.ServerMessage;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

public class WebsocketCommunicator extends Endpoint {
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

        session.addMessageHandler((MessageHandler.Whole<String>) message -> {
            ServerMessage parsedMessage = new Gson().fromJson(message, ServerMessage.class);
            observer.notify(parsedMessage);
        });
    }

    public void close() throws IOException {
        session.close();
    }

    public void send(UserGameCommand message) throws IOException {
        String serializedMessage = new Gson().toJson(message);
        this.session.getBasicRemote().sendText(serializedMessage);
    }

    public void onOpen(Session session, EndpointConfig endpointConfig) {}
}
