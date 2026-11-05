package ui;

public class TerminalBuffer {
    private int width;
    private int height;
    private int anchorRow;
    private int anchorCol;

    private final char[][] buffer;

    StringBuilder outputBuilder;

    public TerminalBuffer(int anchorRow, int anchorCol, int width, int height) {
        this.width = width;
        this.height = height;
        this.anchorRow = anchorRow;
        this.anchorCol = anchorCol;

        buffer = new char[width][height];
        outputBuilder = new StringBuilder((width + 1) * height + 10);
    }

    public void clearBuffer() {
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                buffer[i][j] = ' ';
            }
        }
    }

    public void writeToBuffer(int row, int col, String text) {
        if (row < 0 || row >= height || col < 0 || col >= width) return;

        int length = Math.min(text.length(), width - col);
        for (int j = 0; j < length; j++) {
            buffer[row][col + j] = text.charAt(j);
        }
    }

    public void render() {
        outputBuilder.setLength(0);
        outputBuilder.append(
                EscapeSequences.moveCursorToLocation(anchorRow + 1, anchorCol + 1)
        );

        for (int i = 0; i < height; i++) {
            outputBuilder.append(buffer[i]);

            if (i < height - 1) {
                outputBuilder.append('\n');
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
    }

    public void setAnchor(int anchorRow, int anchorCol) {
        this.anchorRow = anchorRow;
        this.anchorCol = anchorCol;
    }
}
