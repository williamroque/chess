package ui;

import java.util.HashMap;

public class TerminalBuffer {
    private int width;
    private int height;
    private int anchorRow;
    private int anchorCol;

    private char[][] buffer;

    StringBuilder outputBuilder;

    public record Decoration (
            Color backgroundColor,
            Color textColor,
            Style style,
            int limit
    ) {}

    private record CursorPosition (
            int row,
            int col
    ) {}

    HashMap<CursorPosition, Decoration> decorations;

    public enum Color {
        BLACK, LIGHT_GREY, DARK_GREY, RED, GREEN, DARK_GREEN, YELLOW, BLUE, MAGENTA, WHITE
    }

    public enum Style {
        BOLD, FAINT, ITALIC, UNDERLINE, BLINKING
    }

    public TerminalBuffer(int anchorRow, int anchorCol, int width, int height) {
        this.width = width;
        this.height = height;
        this.anchorRow = anchorRow;
        this.anchorCol = anchorCol;

        buffer = new char[height][width];
        outputBuilder = new StringBuilder((width + 1) * height + 10);
        decorations = new HashMap<>();
    }

    public void clearBuffer() {
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                buffer[i][j] = ' ';
            }
        }
    }

    public void writeToBuffer(int row, int col, String text, Decoration decoration) {
        if (row < 0 || row >= height || col < 0 || col >= width) {
            return;
        }

        int length = Math.min(text.length(), width - col);
        for (int j = 0; j < length; j++) {
            buffer[row][col + j] = text.charAt(j);
        }

        if (decoration != null) {
            decorations.put(
                    new CursorPosition(row, col),
                    new Decoration(
                            decoration.backgroundColor(),
                            decoration.textColor(),
                            decoration.style(),
                            length
                    )
            );
        }
    }

    private String buildDecoration(Decoration decoration) {
        StringBuilder output = new StringBuilder();

        if (decoration.backgroundColor != null) {
            output.append(switch (decoration.backgroundColor()) {
                case BLACK -> EscapeSequences.SET_BG_COLOR_BLACK;
                case LIGHT_GREY -> EscapeSequences.SET_BG_COLOR_LIGHT_GREY;
                case DARK_GREY -> EscapeSequences.SET_BG_COLOR_DARK_GREY;
                case RED -> EscapeSequences.SET_BG_COLOR_RED;
                case GREEN -> EscapeSequences.SET_BG_COLOR_GREEN;
                case DARK_GREEN -> EscapeSequences.SET_BG_COLOR_DARK_GREEN;
                case YELLOW -> EscapeSequences.SET_BG_COLOR_YELLOW;
                case BLUE -> EscapeSequences.SET_BG_COLOR_BLUE;
                case MAGENTA -> EscapeSequences.SET_BG_COLOR_MAGENTA;
                case WHITE -> EscapeSequences.SET_BG_COLOR_WHITE;
            });
        }

        if (decoration.textColor() != null) {
            output.append(switch (decoration.textColor()) {
                case BLACK -> EscapeSequences.SET_TEXT_COLOR_BLACK;
                case LIGHT_GREY -> EscapeSequences.SET_TEXT_COLOR_LIGHT_GREY;
                case DARK_GREY -> EscapeSequences.SET_TEXT_COLOR_DARK_GREY;
                case RED -> EscapeSequences.SET_TEXT_COLOR_RED;
                case GREEN -> EscapeSequences.SET_TEXT_COLOR_GREEN;
                case DARK_GREEN -> EscapeSequences.SET_TEXT_COLOR_DARK_GREEN;
                case YELLOW -> EscapeSequences.SET_TEXT_COLOR_YELLOW;
                case BLUE -> EscapeSequences.SET_TEXT_COLOR_BLUE;
                case MAGENTA -> EscapeSequences.SET_TEXT_COLOR_MAGENTA;
                case WHITE -> EscapeSequences.SET_TEXT_COLOR_WHITE;
            });
        }

        if (decoration.style() != null) {
            output.append(switch (decoration.style()) {
                case BOLD -> EscapeSequences.SET_TEXT_BOLD;
                case FAINT -> EscapeSequences.SET_TEXT_FAINT;
                case ITALIC -> EscapeSequences.SET_TEXT_ITALIC;
                case UNDERLINE -> EscapeSequences.SET_TEXT_UNDERLINE;
                case BLINKING -> EscapeSequences.SET_TEXT_BLINKING;
            });
        }

        return output.toString();
    }

    private String resetDecorations() {
        return EscapeSequences.RESET_BG_COLOR
                + EscapeSequences.RESET_TEXT_COLOR
                + EscapeSequences.RESET_TEXT_BLINKING
                + EscapeSequences.RESET_TEXT_BOLD_FAINT
                + EscapeSequences.RESET_TEXT_ITALIC
                + EscapeSequences.RESET_TEXT_UNDERLINE;
    }

    public void render() {
        outputBuilder.setLength(0);

        for (int i = 0; i < height; i++) {
            outputBuilder.append(EscapeSequences.moveCursorToLocation(anchorRow + i + 1, anchorCol + 1));

            int decorationLimit = -1;

            for (int j = 0; j < width; j++) {
                if (decorationLimit == 0) {
                    outputBuilder.append(resetDecorations());
                }

                CursorPosition position = new CursorPosition(i, j);
                Decoration decoration = decorations.get(position);

                if (decoration != null) {
                    outputBuilder.append(
                            buildDecoration(decoration)
                    );
                    decorationLimit = decoration.limit();
                }

                outputBuilder.append(buffer[i][j]);

                decorationLimit--;
            }
        }

        System.out.print(outputBuilder.toString());
        System.out.flush();
    }

    public int[] getSize() {
        return new int[]{width, height};
    }

    public int[] getAnchor() {
        return new int[]{anchorRow, anchorCol};
    }

    public void setSize(int width, int height) {
        this.width = width;
        this.height = height;

        buffer = new char[height][width];
        outputBuilder = new StringBuilder((width + 1) * height + 10);
    }

    public void setAnchor(int anchorRow, int anchorCol) {
        this.anchorRow = anchorRow;
        this.anchorCol = anchorCol;
    }
}
