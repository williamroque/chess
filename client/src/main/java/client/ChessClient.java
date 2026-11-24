package client;

import client.communication.HttpCommunicator;
import client.communication.ServerFacade;
import client.communication.WebsocketCommunicator;
import jakarta.websocket.DeploymentException;
import ui.state.AuthState;
import ui.state.GameState;
import ui.terminal.GameTerminalView;
import ui.terminal.PregameTerminalView;
import ui.terminal.REPLTerminalView;
import ui.terminal.TerminalViewManager;
import websocket.messages.ServerMessage;

import java.io.IOException;
import java.net.URISyntaxException;

public class ChessClient implements ServerMessageObserver {
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

        GameTerminalView gameView = new GameTerminalView(viewManager, facade, authState, gameState);
        viewManager.registerView("game", gameView);

        REPLTerminalView replView = new PregameTerminalView(viewManager, facade, authState, gameState);
        viewManager.registerView("pregame", replView);
        viewManager.switchView("pregame");

        viewManager.exit();
    }

    public void notify(ServerMessage message) {

    }
}
