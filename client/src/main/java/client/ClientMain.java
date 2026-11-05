package client;

import chess.*;
import ui.REPLTerminalView;
import ui.TerminalViewManager;

public class ClientMain {
    public static void main(String[] args) {
        TerminalViewManager viewManager = new TerminalViewManager();
        viewManager.initialize();

        REPLTerminalView replView = new REPLTerminalView();
        viewManager.registerView("repl", replView);
        viewManager.switchView("repl");

        viewManager.exit();
    }
}
