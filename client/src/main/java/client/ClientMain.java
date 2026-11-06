package client;

import chess.*;
import ui.AuthState;
import ui.PregameTerminalView;
import ui.REPLTerminalView;
import ui.TerminalViewManager;

public class ClientMain {
    public static void main(String[] args) {
        int port = 8080;
        ServerFacade facade = new ServerFacade("http://localhost:" + port);

        TerminalViewManager viewManager = new TerminalViewManager();
        viewManager.initialize();

        AuthState authState = new AuthState();

        GameTerminalView gameView = new GameTerminalView(viewManager, facade, authState);
        viewManager.registerView("game", gameView);

        REPLTerminalView replView = new PregameTerminalView(viewManager, facade, authState);
        viewManager.registerView("pregame", replView);
        viewManager.switchView("pregame");

        viewManager.exit();
    }
}
