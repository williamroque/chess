package client;

import client.communication.HttpCommunicator;
import client.communication.ServerFacade;
import client.communication.WebsocketCommunicator;
import jakarta.websocket.DeploymentException;
import ui.state.AuthState;
import ui.state.GameState;
import ui.terminal.*;
import websocket.messages.ErrorMessage;
import websocket.messages.LoadGameMessage;
import websocket.messages.NotificationMessage;
import websocket.messages.ServerMessage;

import java.io.IOException;
import java.net.URISyntaxException;

public class ChessClient implements ServerMessageObserver {
    private GameTerminalView gameView;

    void start() throws DeploymentException, URISyntaxException, IOException {
        int port = 8080;

        HttpCommunicator httpCommunicator = new HttpCommunicator("http://localhost:" + port);
        WebsocketCommunicator websocketCommunicator = new WebsocketCommunicator(
                "ws://localhost:" + port + "/ws",
                this
        );

        ServerFacade facade = new ServerFacade(httpCommunicator, websocketCommunicator);

        TerminalViewManager viewManager = new TerminalViewManager();
        viewManager.initialize();

        AuthState authState = new AuthState();
        GameState gameState = new GameState();

        gameView = new GameTerminalView(viewManager, facade, authState, gameState);
        viewManager.registerView("game", gameView);

        REPLTerminalView replView = new PregameTerminalView(viewManager, facade, authState, gameState);
        viewManager.registerView("pregame", replView);
        viewManager.switchView("pregame");

        viewManager.exit();
    }

    public void notify(ServerMessage message) {
        switch (message.getServerMessageType()) {
            case LOAD_GAME -> {
                LoadGameMessage loadGameMessage = (LoadGameMessage) message;
                gameView.loadGame(loadGameMessage.getGame());
            }
            case ERROR -> {
                ErrorMessage errorMessage = (ErrorMessage) message;
                gameView.showError(errorMessage.getErrorMessage());
                gameView.refreshREPL();
            }
            case NOTIFICATION -> {
                NotificationMessage notificationMessage = (NotificationMessage) message;
                gameView.showNeutral(notificationMessage.getMessage());
                gameView.refreshREPL();
            }
        }
    }
}
