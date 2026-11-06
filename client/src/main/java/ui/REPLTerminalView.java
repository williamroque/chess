package ui;

import client.ServerFacade;

import java.util.*;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;

public class REPLTerminalView extends TerminalView {
    ServerFacade facade;

    protected AuthState authState;
    protected GameState gameState;

    HashMap<String, CommandEntry> commands;

    protected record CommandEntry (
            Consumer<List<String>> callback,
            BooleanSupplier condition
    ) {}

    protected record REPLLine (
            String content,
            TerminalBuffer.Decoration decoration
    ) {}

    protected final ArrayList<REPLLine> replLines;

    public REPLTerminalView(TerminalViewManager manager, ServerFacade facade, AuthState authState, GameState gameState) {
        super(manager);

        this.facade = facade;
        this.authState = authState;
        this.gameState = gameState;

        commands = new HashMap<>();
        replLines = new ArrayList<>();

        initializeCommands();
    }

    protected void registerCommand(String command, Consumer<List<String>> callback, BooleanSupplier condition) {
        commands.put(command, new CommandEntry(callback, condition));
    }

    protected void initializeCommands() { }

    private void parseCommand(String command) throws InvalidCommandException {
        command = command.toLowerCase().trim();

        List<String> commandParts = List.of(command.split(" "));
        String commandBase = commandParts.getFirst();

        CommandEntry entry = commands.get(commandBase);

        if (entry != null && entry.condition().getAsBoolean()) {
            entry.callback().accept(commandParts.subList(1, commandParts.size()));
        } else {
            throw new InvalidCommandException("Invalid command.");
        }
    }

    protected void showError(String message) {
        addLine(
                message,
                new TerminalBuffer.Decoration(
                        null,
                        TerminalBuffer.Color.MAGENTA,
                        TerminalBuffer.Style.BOLD,
                        0
                )
        );
    }

    protected void showSuccess(String message) {
        addLine(
                message,
                new TerminalBuffer.Decoration(
                        null,
                        TerminalBuffer.Color.BLUE,
                        TerminalBuffer.Style.BOLD,
                        0
                )
        );
    }

    protected void renderLines(ArrayList<REPLLine> lines, TerminalBuffer buffer) {
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

    protected void addLine(String content, TerminalBuffer.Decoration decoration) {
        replLines.add(new REPLLine(content, decoration));
    }

    private void appendLine(String content, TerminalBuffer.Decoration decoration) {
        REPLLine lastLine = replLines.removeLast();
        replLines.add(new REPLLine(
                lastLine.content() + content,
                decoration
        ));
    }

    protected void render() {}

    @Override
    protected void eventLoop() {
        Scanner scanner = new Scanner(System.in);

        while (mounted) {
            String loginIndicator = authState.getAuth() == null
                    ? "[Logged out] ♟ ❯"
                    : "[" + authState.getAuth().username() + "] ♚ ❯";

            addLine(
                    loginIndicator + " |",
                    new TerminalBuffer.Decoration(
                            null,
                            null,
                            TerminalBuffer.Style.FAINT,
                            0
                    )
            );

            clearView();
            render();

            if (!scanner.hasNextLine()) {
                break;
            }

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
                showError("Unknown command or invalid usage: `"
                        + line.split(" ")[0]
                        + "`. Type `help` to get a list of possible commands.");
            }
        }
    }
}
