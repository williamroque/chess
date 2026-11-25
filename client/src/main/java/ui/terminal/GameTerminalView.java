package ui.terminal;

import chess.*;
import client.communication.ServerFacade;
import model.GameData;
import ui.state.AuthState;
import ui.state.GameState;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public class GameTerminalView extends REPLTerminalView {
    TerminalBuffer replBuffer;
    TerminalBuffer boardBuffer;

    static Map<ChessPiece.PieceType, String> whitePieces = Map.of(
            ChessPiece.PieceType.KING,   "♔",
            ChessPiece.PieceType.QUEEN,  "♕",
            ChessPiece.PieceType.ROOK,   "♖",
            ChessPiece.PieceType.BISHOP, "♗",
            ChessPiece.PieceType.KNIGHT, "♘",
            ChessPiece.PieceType.PAWN,   "♙"
    );

    static Map<ChessPiece.PieceType, String> blackPieces = Map.of(
            ChessPiece.PieceType.KING,   "♚",
            ChessPiece.PieceType.QUEEN,  "♛",
            ChessPiece.PieceType.ROOK,   "♜",
            ChessPiece.PieceType.BISHOP, "♝",
            ChessPiece.PieceType.KNIGHT, "♞",
            ChessPiece.PieceType.PAWN,   "♟"
    );

    public GameTerminalView(TerminalViewManager manager, ServerFacade facade, AuthState authState, GameState gameState) {
        super(manager, facade, authState, gameState);

        TerminalViewRow boardRow = createRow(12);
        TerminalViewCell boardCell = createCell(boardRow, 0);

        TerminalViewRow replRow = createRow(0);
        TerminalViewCell replCell = createCell(replRow, 0);

        resetLayout();

        boardBuffer = boardCell.buffer();
        replBuffer = replCell.buffer();
    }

    private void renderBoard() {
        if (gameState.getGame() == null) {
            manager.switchView("pregame");
            return;
        }

        ChessPiece[][] board = gameState.getGame()
                .game()
                .getBoard()
                .getBoardConfiguration();

        if (gameState.getTeam() == ChessGame.TeamColor.BLACK) {
            int boardStart = 0;
            int boardEnd = board.length - 1;

            while (boardStart < boardEnd) {
                ChessPiece[] temp = board[boardStart];
                board[boardStart] = board[boardEnd];
                board[boardEnd] = temp;
                boardStart++;
                boardEnd--;
            }

            for (ChessPiece[] row : board) {
                int start = 0;
                int end = board.length - 1;

                while (start < end) {
                    ChessPiece temp = row[start];
                    row[start] = row[end];
                    row[end] = temp;
                    start++;
                    end--;
                }
            }
        }

        TerminalBuffer.Decoration indexDecoration = new TerminalBuffer.Decoration(
                null,
                TerminalBuffer.Color.WHITE,
                TerminalBuffer.Style.FAINT,
                0
        );

        boardBuffer.clearBuffer();

        if (gameState.getTeam() == ChessGame.TeamColor.WHITE) {
            boardBuffer.writeToBuffer(1, 4, "a  b  c  d  e  f  g  h", indexDecoration);
        } else {
            boardBuffer.writeToBuffer(1, 4, "h  g  f  e  d  c  b  a", indexDecoration);
        }

        int currentRow = 2;

        for (ChessPiece[] row : board) {
            int currentCol = 1;

            String currentIndex;

            if (gameState.getTeam() == ChessGame.TeamColor.WHITE) {
                currentIndex = String.valueOf(10 - currentRow);
            } else {
                currentIndex = String.valueOf(currentRow - 1);
            }

            boardBuffer.writeToBuffer(currentRow, 1, currentIndex, indexDecoration);

            for (ChessPiece piece : row) {
                TerminalBuffer.Decoration currentDecoration = getCurrentDecoration(piece, currentRow, currentCol);

                String squareText;

                if (piece == null) {
                    squareText = "   ";
                } else {
                    squareText = " "
                            + (piece.getTeamColor() == ChessGame.TeamColor.WHITE
                            ? whitePieces.get(piece.getPieceType())
                            : blackPieces.get(piece.getPieceType()))
                            + " ";
                }

                boardBuffer.writeToBuffer(currentRow, currentCol * 3, squareText, currentDecoration);

                currentCol++;
            }

            boardBuffer.writeToBuffer(currentRow, currentCol * 3 + 1, currentIndex, indexDecoration);

            currentRow++;
        }

        if (gameState.getTeam() == ChessGame.TeamColor.WHITE) {
            boardBuffer.writeToBuffer(currentRow, 4, "a  b  c  d  e  f  g  h", indexDecoration);
        } else {
            boardBuffer.writeToBuffer(currentRow, 4, "h  g  f  e  d  c  b  a", indexDecoration);
        }

        boardBuffer.render();
    }

    private static TerminalBuffer.Decoration getCurrentDecoration(ChessPiece piece, int currentRow, int currentCol) {
        TerminalBuffer.Decoration blackDecoration = new TerminalBuffer.Decoration(
                TerminalBuffer.Color.BLACK,
                piece == null || piece.getTeamColor() == ChessGame.TeamColor.WHITE ? (
                        TerminalBuffer.Color.BLUE
                ) : (
                        TerminalBuffer.Color.LIGHT_GREY
                ),
                TerminalBuffer.Style.BOLD,
                0
        );

        TerminalBuffer.Decoration whiteDecoration = new TerminalBuffer.Decoration(
                TerminalBuffer.Color.WHITE,
                piece == null || piece.getTeamColor() == ChessGame.TeamColor.WHITE ? (
                        TerminalBuffer.Color.BLUE
                ) : (
                        TerminalBuffer.Color.LIGHT_GREY
                ),
                TerminalBuffer.Style.BOLD,
                0
        );

        TerminalBuffer.Decoration currentDecoration = (currentRow + currentCol) % 2 == 1
                ? whiteDecoration
                : blackDecoration;
        return currentDecoration;
    }

    public void loadGame(GameData data) {
        gameState.setGame(data);
        render();
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

        replLines.addAll(List.of(
                new REPLLine("", body),
                new REPLLine("♟  COMMANDS & USAGE", header),
                new REPLLine("────────────────────────────────────────────────────────────", divider),
                new REPLLine("▸ move     <move>                  Move a piece using algebraic notation", body),
                new REPLLine("▸ legal    <position>              Highlight all legal moves for a piece", body),
                new REPLLine("▸ redraw                           Refresh the chess board", body),
                new REPLLine("▸ leave                            Leave the game without resigning", body),
                new REPLLine("▸ resign                           Resign the game", body),
                new REPLLine("▸ help                             Show available commands", body),
                new REPLLine("────────────────────────────────────────────────────────────", divider),
                new REPLLine(" ", body)
        ));
    }

    private void moveHandler(List<String> arguments) {
        if (arguments.size() != 1) {
            showError("Wrong number of arguments for `move`.");
            showError("▸ move <move>");
            return;
        }

        ChessMove move;

        try {
            move = ChessMove.buildMove(arguments.getFirst());
        } catch (InvalidMoveException e) {
            showError("Invalid chess move. Use algebraic notation (e.g., d2d4).");
            return;
        }

        try {
            facade.makeMove(authState.getAuth().authToken(), gameState.getGame().gameID(), move);
        } catch (Exception e) {
            showError("Connection error.");
        }
    }

    private void leaveHandler(List<String> arguments) {
        if (!arguments.isEmpty()) {
            showError("Wrong number of arguments for `leave`.");
            showError("▸ leave");
            return;
        }

        try {
            facade.leave(authState.getAuth().authToken(), gameState.getGame().gameID());
        } catch (Exception e) {
            showError("Connection error.");
        }

        gameState.clearTeam();
        gameState.clearGame();
        replLines.clear();

        unmount();
    }

    private void resignHandler(List<String> arguments) {
        if (!arguments.isEmpty()) {
            showError("Wrong number of arguments for `resign`.");
            showError("▸ resign");
            return;
        }

        try {
            facade.resign(authState.getAuth().authToken(), gameState.getGame().gameID());
        } catch (Exception e) {
            showError("Connection error.");
        }
    }

    @Override
    protected void initializeCommands() {
        registerCommand("help", this::helpHandler, () -> true);
        registerCommand("leave", this::leaveHandler, () -> true);
        registerCommand("resign", this::resignHandler, () -> true);
        registerCommand("move", this::moveHandler, () -> true);
    }

    @Override
    protected void render() {
        renderBoard();
        renderLines(replLines, replBuffer);
    }

    public void refreshREPL() {
        renderLines(replLines, replBuffer);
    }
}
