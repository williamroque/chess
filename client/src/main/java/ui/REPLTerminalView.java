package ui;

import model.AuthData;

import java.util.*;
import java.util.function.Consumer;

public class REPLTerminalView extends TerminalView {
    TerminalBuffer replBuffer;
    TerminalBuffer welcomeBuffer;

    AuthData currentAuth;

    HashMap<String, Consumer<List<String>>> commands;

    private record REPLLine (
            String content,
            TerminalBuffer.Decoration decoration
    ) {}

    private final ArrayList<REPLLine> replLines;
    private final ArrayList<REPLLine> welcomeLines = buildWelcome();

    private static ArrayList<REPLLine> buildWelcome() {
        TerminalBuffer.Decoration italic = new TerminalBuffer.Decoration(
                null,
                null,
                TerminalBuffer.Style.ITALIC,
                0
        );

        return new ArrayList<>(List.of(
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

    public REPLTerminalView() {
        super();

        TerminalViewRow welcomeRow = createRow(11);
        TerminalViewCell welcomeCell = createCell(welcomeRow, 100);

        TerminalViewRow replRow = createRow(0);
        TerminalViewCell replCell = createCell(replRow, 0);

        resetLayout();

        welcomeBuffer = welcomeCell.buffer();
        replBuffer = replCell.buffer();

        replLines = new ArrayList<>();
        commands = new HashMap<>();
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

    private void renderLines(ArrayList<REPLLine> lines, TerminalBuffer buffer) {
        buffer.clearBuffer();

        int height = buffer.getSize()[1];
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
            buffer.writeToBuffer(i, 0, content, line.decoration());
        }

        buffer.render();

        int[] anchor = buffer.getAnchor();
        moveCursor(
                anchor[0] + cursorPosition[0],
                anchor[1] + cursorPosition[1]
        );
    }

    private void addLine(String content, TerminalBuffer.Decoration decoration) {
        replLines.add(new REPLLine(content, decoration));
    }

    private void appendLine(String content, TerminalBuffer.Decoration decoration) {
        REPLLine lastLine = replLines.removeLast();
        replLines.add(new REPLLine(
                lastLine.content() + content,
                decoration
        ));
    }

    @Override
    protected void eventLoop() {
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

            clearView();
            renderLines(welcomeLines, welcomeBuffer);
            renderLines(replLines, replBuffer);

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
                addLine(
                        "Unknown command or invalid usage: `"
                                + line.split(" ")[0]
                                + "`. Type `help` to get a list of possible commands.",
                        new TerminalBuffer.Decoration(
                                null,
                                TerminalBuffer.Color.MAGENTA,
                                TerminalBuffer.Style.BOLD,
                                0
                        )
                );
            }
        }
    }
}
