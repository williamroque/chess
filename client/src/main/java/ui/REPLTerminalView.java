package ui;

import model.AuthData;

import java.util.*;
import java.util.function.Consumer;

public class REPLTerminalView extends TerminalView {
    TerminalBuffer replBuffer;

    AuthData currentAuth;

    HashMap<String, Consumer<List<String>>> commands;

    ArrayList<String> lines;

    public REPLTerminalView() {
        super();

        TerminalViewRow row = createRow(100);
        TerminalViewCell cell = createCell(row, 100);

        resetLayout();

        replBuffer = cell.buffer();
        lines = new ArrayList<>();
    }

    private void printWelcome() {
        lines.addAll(List.of(
                "Welcome to the CS 240 Chess Game!",
                ">>> Type Help to get started. <<<",
                ""
        ));
    }

    private void registerCommand(String command, Consumer<List<String>> callback) {
        commands.put(command, callback);
    }

    private void parseCommand(String command) throws InvalidCommandException {
        command = command.toLowerCase().trim();

        List<String> commandParts = List.of(command.split(" "));

        if (commands.containsKey(commandParts.getFirst())) {
            Consumer<List<String>> callback = commands.get(command);
            callback.accept(commandParts.subList(1, commandParts.size()));
        } else {
            throw new InvalidCommandException("Invalid command.");
        }
    }

    protected void renderLines() {
        replBuffer.clearBuffer();
        clearView();

        int height = replBuffer.getSize()[1];
        int limit = Math.min(lines.size(), height);
        List<String> visibleLines = lines.subList(lines.size() - limit, lines.size());

        int[] cursorPosition = {0, 0};

        for (int i = 0; i < visibleLines.size(); i++) {
            String line = visibleLines.get(i);

            int pipeIndex = line.indexOf('|');
            if (pipeIndex > -1) {
                cursorPosition[0] = i;
                cursorPosition[1] = pipeIndex;

                line = line.replace("|", "");
            }
            replBuffer.writeToBuffer(i, 0, line);
        }

        replBuffer.render();

        int[] anchor = replBuffer.getAnchor();
        moveCursor(
                anchor[0] + cursorPosition[0],
                anchor[1] + cursorPosition[1]
        );
    }

    private void addLines(String... newLines) {
        lines.addAll(Arrays.asList(newLines));
        renderLines();
    }

    @Override
    protected void eventLoop() {
        printWelcome();

        Scanner scanner = new Scanner(System.in);

        while (mounted) {
            addLines(">>> |");

            if (!scanner.hasNextLine()) break;

            String line = scanner.nextLine();

            try {
                parseCommand(line);
            } catch (InvalidCommandException _) {

            }
        }
    }
}
