package ui;

import chess.ChessGame;
import client.ServerFacade;
import client.exceptions.FacadeException;
import model.GameData;
import model.GameDataList;
import model.UserData;

import java.util.ArrayList;
import java.util.List;

public class PregameTerminalView extends REPLTerminalView {
    TerminalBuffer replBuffer;
    TerminalBuffer welcomeBuffer;

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

    public PregameTerminalView(TerminalViewManager manager, ServerFacade facade, AuthState authState, GameState gameState) {
        super(manager, facade, authState, gameState);

        TerminalViewRow welcomeRow = createRow(11);
        TerminalViewCell welcomeCell = createCell(welcomeRow, 0);

        TerminalViewRow replRow = createRow(0);
        TerminalViewCell replCell = createCell(replRow, 0);

        resetLayout();

        welcomeBuffer = welcomeCell.buffer();
        replBuffer = replCell.buffer();
    }

    private void helpHandler(List<String> arguments) {
        TerminalBuffer.Decoration header = new TerminalBuffer.Decoration(
                null,
                TerminalBuffer.Color.WHITE,
                null,
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

        if (authState.getAuth() == null)  {
            replLines.addAll(List.of(
                    new REPLLine("", body),
                    new REPLLine("♟  COMMANDS & USAGE", header),
                    new REPLLine("────────────────────────────────────────────────────────────", divider),
                    new REPLLine("▸ register <user> <pass> <email>   Create an account", body),
                    new REPLLine("▸ login    <user> <pass>           Sign in to play", body),
                    new REPLLine("▸ help                             Show available commands", body),
                    new REPLLine("▸ quit                             Resign / exit the program", body),
                    new REPLLine("────────────────────────────────────────────────────────────", divider),
                    new REPLLine(" ", body)
            ));
        } else {
            replLines.addAll(List.of(
                    new REPLLine("", body),
                    new REPLLine("♟  COMMANDS & USAGE", header),
                    new REPLLine("────────────────────────────────────────────────────────────", divider),
                    new REPLLine("▸ create <name>                    Create a new game", body),
                    new REPLLine("▸ list                             List games on the server", body),
                    new REPLLine("▸ join <id> <WHITE/BLACK>          Join a game with a color by ID", body),
                    new REPLLine("▸ observe <id>                     Observe a game in real time", body),
                    new REPLLine("▸ logout                           Log out of your account", body),
                    new REPLLine("▸ quit                             Resign / exit the program", body),
                    new REPLLine("▸ help                             Show available commands", body),
                    new REPLLine("────────────────────────────────────────────────────────────", divider),
                    new REPLLine(" ", body)
            ));
        }
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
            authState.setAuth(facade.register(new UserData(
                    arguments.get(0),
                    arguments.get(1),
                    arguments.get(2)
            )));
            showSuccess("Your new account has been created.");
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
            authState.setAuth(facade.login(new UserData(
                    arguments.get(0),
                    arguments.get(1),
                    null
            )));
            showSuccess("Logged in as " + arguments.get(0) + ".");
        } catch (FacadeException e) {
            showError(e.getMessage());
        }
    }

    private void createHandler(List<String> arguments) {
        if (arguments.size() != 1) {
            showError("Wrong number of arguments for `create`.");
            showError("▸ create <name>");
            return;
        }

        try {
            facade.createGame(authState.getAuth(), arguments.getFirst());
            showSuccess("Created a new game with the name of `" + arguments.getFirst() + "`.");
        } catch (FacadeException e) {
            showError(e.getMessage());
        }
    }

    private void listHandler(List<String> arguments) {
        if (!arguments.isEmpty()) {
            showError("Wrong number of arguments for `list`.");
            showError("▸ list");
            return;
        }

        TerminalBuffer.Decoration header = new TerminalBuffer.Decoration(
                null,
                TerminalBuffer.Color.WHITE,
                TerminalBuffer.Style.BOLD,
                0
        );
        TerminalBuffer.Decoration body = new TerminalBuffer.Decoration(
                null,
                null,
                null,
                0
        );

        try {
            GameDataList games = facade.listGames(authState.getAuth());
            int lineWidth = 42;
            int lineInnerWidth = lineWidth - 8;

            addLine("╭── Game List ───────────────────────────╮", header);
            addLine("│                                        │", header);

            for (int i = 0; i < games.games().size(); i++) {
                String gameName = games.games().stream().toList().get(i).gameName();
                gameName = (i + 1) + ". " + gameName;

                if (gameName.length() > lineInnerWidth) {
                    gameName = gameName.substring(0, lineInnerWidth - 3) + "...";
                }

                gameName = String.format("%-" + lineInnerWidth + "s", gameName);

                String content = "│   " + gameName + "   │";

                addLine(content, body);
            }

            addLine("│                                        │", header);
            addLine("╰────────────────────────────────────────╯", header);
        } catch (FacadeException e) {
            showError(e.getMessage());
        }
    }

    private void joinHandler(List<String> arguments) {
        if (arguments.size() != 2) {
            showError("Wrong number of arguments for `join`.");
            showError("▸ join <id> <WHITE/BLACK>");
            return;
        }

        int gameID = 0;

        try {
            gameID = Integer.parseInt(arguments.getFirst());
        } catch (NumberFormatException _) {
            showError("Game ID should be a number.");
            return;
        }

        try {
            List<GameData> games = facade.listGames(authState.getAuth()).games().stream().toList();

            if (gameID < 1 || gameID > games.size()) {
                showError("Invalid game ID. Run `list` to see valid IDs.");
                return;
            }

            GameData game = facade.joinGame(
                    authState.getAuth(),
                    arguments.getLast().toUpperCase(),
                    games.get(gameID - 1).gameID()
            );

            gameState.setGame(game);
            gameState.setTeam(
                    arguments.getLast().equalsIgnoreCase("WHITE")
                    ? ChessGame.TeamColor.WHITE
                    : ChessGame.TeamColor.BLACK
            );
            manager.switchView("game");
        } catch (FacadeException e) {
            showError(e.getMessage());
        }
    }

    private void observeHandler(List<String> arguments) {
        if (arguments.size() != 1) {
            showError("Wrong number of arguments for `observe`.");
            showError("▸ observe <id>");
            return;
        }

        int gameID = 0;

        try {
            gameID = Integer.parseInt(arguments.getFirst());
        } catch (NumberFormatException _) {
            showError("Game ID should be a number.");
            return;
        }

        try {
            List<GameData> games = facade.listGames(authState.getAuth()).games().stream().toList();

            if (gameID < 1 || gameID > games.size()) {
                showError("Invalid game ID. Run `list` to see valid IDs.");
                return;
            }

            GameData game = games.get(gameID - 1);

            gameState.setGame(game);
            gameState.clearTeam();
            manager.switchView("game");
        } catch (FacadeException e) {
            showError(e.getMessage());
        }
    }


    @Override
    protected void initializeCommands() {
        registerCommand("help", this::helpHandler, () -> true);
        registerCommand("quit", this::quitHandler, () -> authState.getAuth() == null);

        registerCommand("register", this::registerHandler, () -> authState.getAuth() == null);
        registerCommand("login", this::loginHandler, () -> authState.getAuth() == null);

        registerCommand("create", this::createHandler, () -> authState.getAuth() != null);
        registerCommand("list", this::listHandler, () -> authState.getAuth() != null);
        registerCommand("join", this::joinHandler, () -> authState.getAuth() != null);
        registerCommand("observe", this::observeHandler, () -> authState.getAuth() != null);
//        registerCommand("logout", this::logoutHanlder, () -> authState.getAuth() != null);
    }

    @Override
    protected void render() {
        renderLines(welcomeLines, welcomeBuffer);
        renderLines(replLines, replBuffer);
    }
}
