package ui;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class TerminalView {
    protected ArrayList<TerminalViewRow> viewRows;

    protected int width;
    protected int height;

    protected boolean mounted = false;

    private static int[] getTerminalSize() {
        int[] size = new int[]{80, 24}; // Default fallback if commands fail

        try {
            ProcessBuilder processBuilder;

            if (System.getProperty("os.name").toLowerCase().contains("win")) {

                processBuilder = new ProcessBuilder("cmd", "/c", "mode con");
                Process process = processBuilder.start();
                BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));

                String line;
                while ((line = reader.readLine()) != null) {
                    if (line.contains("Columns:")) size[0] = Integer.parseInt(line.split(":")[1].trim());
                    if (line.contains("Lines:"))   size[1] = Integer.parseInt(line.split(":")[1].trim());
                }
            } else {
                processBuilder = new ProcessBuilder("sh", "-c", "stty size < /dev/tty");
                Process process = processBuilder.start();
                BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));

                String[] output = reader.readLine().split(" ");
                size[1] = Integer.parseInt(output[0]);
                size[0] = Integer.parseInt(output[1]);
            }
        } catch (Exception e) {
            // Silently fall back to default 80x24
        }

        return size;
    }

    public TerminalView() {
        viewRows = new ArrayList<>();

        int[] terminalSize = getTerminalSize();
        width = terminalSize[0];
        height = terminalSize[1];
    }

    protected TerminalViewRow createRow(int height) {
        TerminalViewRow row = new TerminalViewRow(height, new ArrayList<>());
        this.viewRows.add(row);

        return row;
    }

    protected TerminalViewCell createCell(TerminalViewRow row, int width) {
        TerminalBuffer buffer = new TerminalBuffer(0, 0, 0, 0);

        TerminalViewCell cell = new TerminalViewCell(width, buffer);
        row.row().add(cell);

        return cell;
    }

    protected void clearView() {
        System.out.print(EscapeSequences.ERASE_SCREEN);
        System.out.print(EscapeSequences.RESET_CURSOR);
        System.out.flush();
    }

    protected void moveCursor(int row, int col) {
        System.out.print(EscapeSequences.moveCursorToLocation(row + 1, col + 1));
        System.out.flush();
    }

    protected void resetLayout() {
        int totalRowHeights = viewRows.stream()
                .map(TerminalViewRow::height)
                .reduce(0, Integer::sum);

        int rowOffset = 0;

        for (TerminalViewRow row : viewRows) {
            int rowHeight = row.height() == 0 ? height - totalRowHeights : row.height();

            int totalCellWidths = row.row().stream()
                    .map(TerminalViewCell::width)
                    .reduce(0, Integer::sum);

            int cellOffset = 0;

            for (TerminalViewCell cell : row.row()) {
                int cellWidth = cell.width() == 0 ? width - totalCellWidths : cell.width();

                cell.buffer().setAnchor(rowOffset, cellOffset);
                cell.buffer().setSize(cellWidth, rowHeight);

                cellOffset += cellWidth;
            }

            rowOffset += rowHeight;
        }
    }

    protected void eventLoop() {}

    public void mount() {
        mounted = true;
        eventLoop();
    }

    public void unmount() {
        mounted = false;
    }
}
