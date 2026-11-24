package client;

import client.communication.HttpCommunicator;
import client.communication.ServerFacade;
import ui.state.AuthState;
import ui.state.GameState;
import ui.terminal.GameTerminalView;
import ui.terminal.PregameTerminalView;
import ui.terminal.REPLTerminalView;
import ui.terminal.TerminalViewManager;

public class ChessClient {
    void start() {
        int port = 8080;

        HttpCommunicator httpCommunicator = new HttpCommunicator("http://localhost:" + port);
        ServerFacade facade = new ServerFacade(httpCommunicator);

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
}
