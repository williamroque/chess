package client;

import chess.ChessGame;
import chess.ChessPiece;
import ui.*;

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

        TerminalBuffer.Decoration blackDecoration = new TerminalBuffer.Decoration(
                TerminalBuffer.Color.BLACK,
                TerminalBuffer.Color.WHITE,
                TerminalBuffer.Style.BOLD,
                0
        );

        TerminalBuffer.Decoration whiteDecoration = new TerminalBuffer.Decoration(
                TerminalBuffer.Color.WHITE,
                TerminalBuffer.Color.BLACK,
                TerminalBuffer.Style.BOLD,
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
                TerminalBuffer.Decoration currentDecoration = (currentRow + currentCol) % 2 == 1
                        ? whiteDecoration
                        : blackDecoration;

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

    private void quitHandler(List<String> arguments) {
        gameState.clearTeam();
        gameState.clearGame();
        unmount();
    }

    @Override
    protected void initializeCommands() {
        registerCommand("quit", this::quitHandler, () -> true);
    }

    @Override
    protected void render() {
        renderBoard();
        renderLines(replLines, replBuffer);
    }
}
