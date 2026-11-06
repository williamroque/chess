package client;

import chess.*;
import ui.*;

public class ClientMain {
    public static void main(String[] args) {
        int port = 8080;
        ServerFacade facade = new ServerFacade("http://localhost:" + port);

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
