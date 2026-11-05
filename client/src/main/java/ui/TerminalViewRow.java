package ui;

import java.util.ArrayList;

public record TerminalViewRow(
        int relativeHeight,
        ArrayList<TerminalViewCell> row
) {}
