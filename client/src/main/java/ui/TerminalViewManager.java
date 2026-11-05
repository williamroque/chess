package ui;

import java.util.HashMap;

public class TerminalViewManager {
    HashMap<String, TerminalView> views;
    String currentView;

    public TerminalViewManager() {
        views = new HashMap<>();
    }

    public void registerView(String name, TerminalView view) {
        views.put(name, view);
    }

    public void switchView(String name) {
        System.out.print(EscapeSequences.ERASE_SCREEN);
        System.out.flush();

        if (currentView != null) {
            views.get(currentView).unmount();
        }

        views.get(name).mount();
        currentView = name;
    }

    public void initialize() {
        System.out.print(EscapeSequences.ENTER_ALT_SCREEN);
        System.out.flush();
    }

    public void exit() {
        System.out.print(EscapeSequences.EXIT_ALT_SCREEN);
        System.out.flush();
    }
}
