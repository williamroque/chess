package ui.terminal;

import chess.*;
import client.communication.ServerFacade;
import model.GameData;
import ui.state.AuthState;
import ui.state.GameState;

import java.io.IOException;
import java.util.*;

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

    private final HashSet<ChessPosition> legalHighlights = new HashSet<>();

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

        ChessBoard board = gameState.getGame()
                .game()
                .getBoard();

        ArrayList<ArrayList<ChessPosition>> boardPositions = board.getBoardPositions(
                gameState.getTeam() == null ? ChessGame.TeamColor.WHITE : gameState.getTeam()
        );

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

        for (ArrayList<ChessPosition> row : boardPositions) {
            int currentCol = 1;

            String currentIndex;

            if (gameState.getTeam() == ChessGame.TeamColor.WHITE) {
                currentIndex = String.valueOf(10 - currentRow);
            } else {
                currentIndex = String.valueOf(currentRow - 1);
            }

            boardBuffer.writeToBuffer(currentRow, 1, currentIndex, indexDecoration);

            for (ChessPosition position : row) {
                ChessPiece piece = board.getPiece(position);

                TerminalBuffer.Decoration currentDecoration = getCurrentDecoration(piece, position);

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

    private TerminalBuffer.Decoration getCurrentDecoration(ChessPiece piece, ChessPosition position) {
        boolean isHighlighted = legalHighlights.contains(position);

        TerminalBuffer.Decoration blackDecoration = new TerminalBuffer.Decoration(
                isHighlighted ? TerminalBuffer.Color.YELLOW : TerminalBuffer.Color.BLACK,
                piece == null || piece.getTeamColor() == ChessGame.TeamColor.WHITE ? (
                        TerminalBuffer.Color.BLUE
                ) : (
                        TerminalBuffer.Color.LIGHT_GREY
                ),
                TerminalBuffer.Style.BOLD,
                0
        );

        TerminalBuffer.Decoration whiteDecoration = new TerminalBuffer.Decoration(
                isHighlighted ? TerminalBuffer.Color.YELLOW : TerminalBuffer.Color.WHITE,
                piece == null || piece.getTeamColor() == ChessGame.TeamColor.WHITE ? (
                        TerminalBuffer.Color.BLUE
                ) : (
                        TerminalBuffer.Color.LIGHT_GREY
                ),
                TerminalBuffer.Style.BOLD,
                0
        );

        return (position.getRow() + position.getColumn()) % 2 == 0
                ? whiteDecoration
                : blackDecoration;
    }

    public void loadGame(GameData data) {
        gameState.setGame(data);
        render();
    }

    private void helpHandler(List<String> arguments) {
        addHelpTable(List.of(
                new HelpTableRow("move",   List.of("<move>"),       "Move a piece using algebraic notation"),
                new HelpTableRow("legal",  List.of("<position>"),   "Highlight all legal moves for a piece"),
                new HelpTableRow("redraw", List.of(),                   "Refresh the chess board"),
                new HelpTableRow("leave",  List.of(),                   "Leave the game without resigning"),
                new HelpTableRow("resign", List.of(),                   "Resign the game"),
                new HelpTableRow("help",   List.of(),                   "Show available commands")
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

        legalHighlights.clear();

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

    private void redrawHandler(List<String> arguments) {
        if (!arguments.isEmpty()) {
            showError("Wrong number of arguments for `redraw`.");
            showError("▸ redraw");
            return;
        }

        legalHighlights.clear();
        renderBoard();
    }

    private void legalMovesHandler(List<String> arguments) {
        if (arguments.size() != 1) {
            showError("Wrong number of arguments for `legal`.");
            showError("▸ legal <position>");
            return;
        }

        ChessPosition position;

        try {
            position = ChessBoard.algebraicToMatrix(arguments.getFirst());
        } catch (InvalidMoveException e) {
            showError(e.getMessage());
            return;
        }

        Collection<ChessMove> moves = gameState.getGame().game().validMoves(position);

        if (moves == null) {
            showError("Error: There are no valid moves for this position");
            return;
        }

        legalHighlights.clear();

        for (ChessMove move : moves) {
            legalHighlights.add(move.getEndPosition());
        }

        renderBoard();
    }

    @Override
    protected void initializeCommands() {
        registerCommand("help", this::helpHandler, () -> true);
        registerCommand("leave", this::leaveHandler, () -> true);
        registerCommand("resign", this::resignHandler, () -> true);
        registerCommand("redraw", this::redrawHandler, () -> true);
        registerCommand("move", this::moveHandler, () -> true);
        registerCommand("legal", this::legalMovesHandler, () -> true);
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
