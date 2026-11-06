package ui;

import model.AuthData;

import java.util.*;
import java.util.function.Consumer;

public class REPLTerminalView extends TerminalView {
    TerminalBuffer replBuffer;

    AuthData currentAuth;

    HashMap<String, Consumer<List<String>>> commands;

    private record REPLLine (
            String content,
            TerminalBuffer.Decoration decoration
    ) {}

    ArrayList<REPLLine> lines;

    public REPLTerminalView() {
        super();

        TerminalViewRow row = createRow(100);
        TerminalViewCell cell = createCell(row, 100);

        resetLayout();

        replBuffer = cell.buffer();
        lines = new ArrayList<>();
        commands = new HashMap<>();
    }

    private void printWelcome() {
        TerminalBuffer.Decoration italic = new TerminalBuffer.Decoration(
                null,
                null,
                TerminalBuffer.Style.ITALIC,
                0
        );

        lines.addAll(List.of(
                new REPLLine("┌──────────────────────────────────────────────┐", null),
                new REPLLine("│  ♟   CS 240 CHESS — TERMINAL EDITION   ♙     │", null),
                new REPLLine("├──────────────────────────────────────────────┤", null),
                new REPLLine("│                                              │", null),
                new REPLLine("│   \"The supreme art of war is to              │", italic),
                new REPLLine("│    subdue the enemy without fighting.\"       │", italic),
                new REPLLine("│                                              │", null),
                new REPLLine("│   ▸ help   Show commands & rules             │", null),
                new REPLLine("│                                              │", null),
                new REPLLine("└──────────────────────────────────────────────┘", null),
                new REPLLine("", null)
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
        List<REPLLine> visibleLines = lines.subList(lines.size() - limit, lines.size());

        int[] cursorPosition = {0, 0};

        for (int i = 0; i < visibleLines.size(); i++) {
            REPLLine line = visibleLines.get(i);
            String content = line.content();

            int pipeIndex = content.indexOf('|');
            if (pipeIndex > -1) {
                cursorPosition[0] = i;
                cursorPosition[1] = pipeIndex;

                content = content.replace("|", "");
            }
            replBuffer.writeToBuffer(i, 0, content, line.decoration());
        }

        replBuffer.render();

        int[] anchor = replBuffer.getAnchor();
        moveCursor(
                anchor[0] + cursorPosition[0],
                anchor[1] + cursorPosition[1]
        );
    }

    private void addLine(String content, TerminalBuffer.Decoration decoration) {
        lines.add(new REPLLine(content, decoration));
        renderLines();
    }

    private void appendLine(String content, TerminalBuffer.Decoration decoration) {
        REPLLine lastLine = lines.removeLast();
        lines.add(new REPLLine(
                lastLine.content() + content,
                decoration
        ));
        renderLines();
    }

    @Override
    protected void eventLoop() {
        printWelcome();

        Scanner scanner = new Scanner(System.in);

        while (mounted) {
            addLine(
                    ">>> |",
                    new TerminalBuffer.Decoration(
                            null,
                            null,
                            TerminalBuffer.Style.FAINT,
                            0
                    )
            );

            if (!scanner.hasNextLine()) break;

            String line = scanner.nextLine();
            appendLine(
                    line,
                    new TerminalBuffer.Decoration(
                            null,
                            null,
                            TerminalBuffer.Style.FAINT,
                            0
                    )
            );

            try {
                parseCommand(line);
            } catch (InvalidCommandException _) {

            }
        }
    }
}
