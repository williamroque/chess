package ui.terminal;

import java.util.ArrayList;

public record TerminalViewRow(
        int height,
        ArrayList<TerminalViewCell> row
) {}
