package chess;

import chess.pieces.*;

import java.util.Map;
import java.util.Objects;

/**
 * A chessboard that can hold and rearrange chess pieces.
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessBoard {
    private ChessPiece[][] board;

    private static final Map<String, Class<? extends ChessPiece>> pieceMap = Map.of(
            "R", Rook.class,
            "N", Knight.class,
            "B", Bishop.class,
            "Q", Queen.class,
            "K", King.class,
            "P", Pawn.class
    );
    private static final String[] DEFAULT_CONFIGURATION = {
            "B:R:a8", "B:N:b8", "B:B:c8", "B:Q:d8", "B:K:e8", "B:B:f8", "B:N:g8", "B:R:h8",
            "B:P:a7", "B:P:b7", "B:P:c7", "B:P:d7", "B:P:e7", "B:P:f7", "B:P:g7", "B:P:h7",
            "W:P:a2", "W:P:b2", "W:P:c2", "W:P:d2", "W:P:e2", "W:P:f2", "W:P:g2", "W:P:h2",
            "W:R:a1", "W:N:b1", "W:B:c1", "W:Q:d1", "W:K:e1", "W:B:f1", "W:N:g1", "W:R:h1",
    };

    public static ChessPosition algebraicToPosition(String position) {
        String files = "abcdefgh";

        int col = files.indexOf(position.charAt(0));
        int row = 8 - Integer.parseInt(String.valueOf(position.charAt(1)));

        return new ChessPosition(row, col);
    }

    public ChessBoard() {
        this.board = new ChessPiece[8][8];
    }

    /**
     * Adds a chess piece to the chessboard
     *
     * @param position where to add the piece to
     * @param piece    the piece to add
     */
    public void addPiece(ChessPosition position, ChessPiece piece) {
        this.board[position.getRow()][position.getColumn()] = piece;
    }

    /**
     * Gets a chess piece on the chessboard
     *
     * @param position The position to get the piece from
     * @return Either the piece at the position, or null if no piece is at that
     * position
     */
    public ChessPiece getPiece(ChessPosition position) {
        throw new RuntimeException("Not implemented");
    }

    /**
     * Sets the board to the default starting board
     * (How the game of chess normally starts)
     */
    public void resetBoard() {
        for (String configuration : DEFAULT_CONFIGURATION) {
            String[] configurationArray = configuration.split(":");

            ChessGame.TeamColor color = configurationArray[0].equals("B") ?
                    ChessGame.TeamColor.BLACK
                    : ChessGame.TeamColor.WHITE;

            ChessPosition position = algebraicToPosition(configurationArray[2]);

            Class<? extends ChessPiece> pieceClass = pieceMap.get(configurationArray[1]);

            if (pieceClass == null) continue;

            try {
                ChessPiece piece = pieceClass
                        .getDeclaredConstructor(ChessGame.TeamColor.class)
                        .newInstance(color);
                this.addPiece(position, piece);
            } catch (Exception _) { }
        }
    }
}
