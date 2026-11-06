package client;

import chess.*;
import ui.REPLTerminalView;
import ui.TerminalViewManager;

public class ClientMain {
    public static void main(String[] args) {
        int port = 8080;
        ServerFacade facade = new ServerFacade("http://localhost:" + port);

        TerminalViewManager viewManager = new TerminalViewManager();
        viewManager.initialize();

        REPLTerminalView replView = new REPLTerminalView(facade);
        viewManager.registerView("repl", replView);
        viewManager.switchView("repl");

        viewManager.exit();
    }
}
