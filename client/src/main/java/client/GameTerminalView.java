package client;

import ui.*;

import java.util.List;

public class GameTerminalView extends REPLTerminalView {
    TerminalBuffer replBuffer;
    TerminalBuffer boardBuffer;

    public GameTerminalView(TerminalViewManager manager, ServerFacade facade, AuthState authState) {
        super(manager, facade, authState);

        TerminalViewRow boardRow = createRow(20);
        TerminalViewCell boardCell = createCell(boardRow, 0);

        TerminalViewRow replRow = createRow(0);
        TerminalViewCell replCell = createCell(replRow, 0);

        resetLayout();

        boardBuffer = boardCell.buffer();
        replBuffer = replCell.buffer();
    }

    private void quitHandler(List<String> arguments) {
        unmount();
    }

    @Override
    protected void initializeCommands() {
        registerCommand("quit", this::quitHandler, () -> authState.getAuth() == null);
    }

    @Override
    protected void render() {
        renderLines(replLines, replBuffer);
    }
}
