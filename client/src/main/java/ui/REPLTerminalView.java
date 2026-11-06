package ui;

import client.ServerFacade;
import client.exceptions.FacadeException;
import model.AuthData;
import model.UserData;

import java.util.*;
import java.util.function.Consumer;

public class REPLTerminalView extends TerminalView {
    ServerFacade facade;

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

    public REPLTerminalView(ServerFacade facade) {
        super();

        this.facade = facade;

        TerminalViewRow welcomeRow = createRow(11);
        TerminalViewCell welcomeCell = createCell(welcomeRow, 100);

        TerminalViewRow replRow = createRow(0);
        TerminalViewCell replCell = createCell(replRow, 0);

        resetLayout();

        welcomeBuffer = welcomeCell.buffer();
        replBuffer = replCell.buffer();

        replLines = new ArrayList<>();
        commands = new HashMap<>();

        initializeCommands();
    }

    private void registerCommand(String command, Consumer<List<String>> callback) {
        commands.put(command, callback);
    }

    private void helpHandler(List<String> arguments) {
        TerminalBuffer.Decoration header = new TerminalBuffer.Decoration(
                null,
                TerminalBuffer.Color.WHITE,
                TerminalBuffer.Style.BOLD,
                0
        );
        TerminalBuffer.Decoration divider = new TerminalBuffer.Decoration(
                null,
                TerminalBuffer.Color.DARK_GREY,
                TerminalBuffer.Style.FAINT,
                0
        );
        TerminalBuffer.Decoration body = new TerminalBuffer.Decoration(
                null,
                TerminalBuffer.Color.BLUE,
                null,
                0
        );

         replLines.addAll(List.of(
                 new REPLLine("♟  COMMANDS & USAGE", header),
                 new REPLLine("────────────────────────────────────────────────────────────", divider),
                 new REPLLine("▸ register <user> <pass> <email>   Create an account", body),
                 new REPLLine("▸ login    <user> <pass>           Sign in to play", body),
                 new REPLLine("▸ help                             Show available commands", body),
                 new REPLLine("▸ quit                             Resign / exit the program", body),
                 new REPLLine("────────────────────────────────────────────────────────────", divider)
        ));
    }

    private void quitHandler(List<String> arguments) {
        unmount();
    }

    private void registerHandler(List<String> arguments) {
        if (arguments.size() != 3) {
            showError("Wrong number of arguments for `register`.");
            showError("▸ register <user> <pass> <email>");
            return;
        }

        try {
            currentAuth = facade.register(new UserData(
                    arguments.get(0),
                    arguments.get(1),
                    arguments.get(2)
            ));
        } catch (FacadeException e) {
            showError(e.getMessage());
        }
    }

    private void loginHandler(List<String> arguments) {
        if (arguments.size() != 2) {
            showError("Wrong number of arguments for `login`.");
            showError("▸ register <user> <pass>");
            return;
        }

        try {
            currentAuth = facade.login(new UserData(
                    arguments.get(0),
                    arguments.get(1),
                    null
            ));
        } catch (FacadeException e) {
            showError(e.getMessage());
        }
    }

    private void initializeCommands() {
        registerCommand("help", this::helpHandler);
        registerCommand("quit", this::quitHandler);
        registerCommand("register", this::registerHandler);
        registerCommand("login", this::loginHandler);
    }

    private void parseCommand(String command) throws InvalidCommandException {
        command = command.toLowerCase().trim();

        List<String> commandParts = List.of(command.split(" "));
        String commandBase = commandParts.getFirst();

        if (commands.containsKey(commandBase)) {
            Consumer<List<String>> callback = commands.get(commandBase);
            callback.accept(commandParts.subList(1, commandParts.size()));
        } else {
            throw new InvalidCommandException("Invalid command.");
        }
    }

    private void showError(String message) {
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
            String loginIndicator = currentAuth == null
                    ? "[Logged out] ♟ ❯"
                    : "[" + currentAuth.username() + "] ♚ ❯";

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
                showError("Unknown command or invalid usage: `"
                        + line.split(" ")[0]
                        + "`. Type `help` to get a list of possible commands.");
            }
        }
    }
}
