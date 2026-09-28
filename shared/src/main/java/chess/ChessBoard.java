package chess;

import java.util.Arrays;
import java.util.Map;
import java.util.Objects;

import static java.lang.Integer.parseInt;

/**
 * A chessboard that can hold and rearrange chess pieces.
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessBoard {
    private ChessPiece[][] board;

    private static final int BOARD_WIDTH = 8;
    private static final int BOARD_HEIGHT = 8;

    private static final String[] DEFAULT_CONFIGURATION = {
            "B:R:a8", "B:N:b8", "B:B:c8", "B:Q:d8", "B:K:e8", "B:B:f8", "B:N:g8", "B:R:h8",
            "B:P:a7", "B:P:b7", "B:P:c7", "B:P:d7", "B:P:e7", "B:P:f7", "B:P:g7", "B:P:h7",
            "W:P:a2", "W:P:b2", "W:P:c2", "W:P:d2", "W:P:e2", "W:P:f2", "W:P:g2", "W:P:h2",
            "W:R:a1", "W:N:b1", "W:B:c1", "W:Q:d1", "W:K:e1", "W:B:f1", "W:N:g1", "W:R:h1"
    };

    private static final Map<String, ChessPiece.PieceType> pieceRegistry = Map.of(
            "B", ChessPiece.PieceType.BISHOP,
            "K", ChessPiece.PieceType.KING,
            "N", ChessPiece.PieceType.KNIGHT,
            "P", ChessPiece.PieceType.PAWN,
            "Q", ChessPiece.PieceType.QUEEN,
            "R", ChessPiece.PieceType.ROOK
    );

    public ChessBoard() {
        this.board = new ChessPiece[BOARD_HEIGHT][BOARD_WIDTH];
    }

    private ChessPosition algebraicToMatrix(String notation) {
        String files = "abcdefgh";

        int row = parseInt(String.valueOf(notation.charAt(1)));
        int col = files.indexOf(notation.charAt(0)) + 1;

        return new ChessPosition(row, col);
    }

    public boolean isValidPosition(ChessPosition position) {
        int row = position.getRow();
        int col = position.getColumn();

        return row > 0 && row <= BOARD_HEIGHT && col > 0 && col <= BOARD_WIDTH;
    }

    /**
     * Adds a chess piece to the chessboard
     *
     * @param position where to add the piece to
     * @param piece    the piece to add
     */
    public void addPiece(ChessPosition position, ChessPiece piece) throws IndexOutOfBoundsException {
        if (this.isValidPosition(position)) {
            this.board[BOARD_HEIGHT - position.getRow()][position.getColumn() - 1] = piece;
        } else {
            throw new IndexOutOfBoundsException("Position is out of bounds: " + position.toString());
        }
    }

    /**
     * Gets a chess piece on the chessboard
     *
     * @param position The position to get the piece from
     * @return Either the piece at the position, or null if no piece is at that
     * position
     */
    public ChessPiece getPiece(ChessPosition position) {
        if (this.isValidPosition(position)) {
            return this.board[BOARD_HEIGHT - position.getRow()][position.getColumn() - 1];
        } else {
            throw new IndexOutOfBoundsException("Position is out of bounds: " + position.toString());
        }
    }

    /**
     * Sets the board to the default starting board
     * (How the game of chess normally starts)
     */
    public void resetBoard() {
        this.board = new ChessPiece[BOARD_HEIGHT][BOARD_WIDTH];

        for (String configuration : DEFAULT_CONFIGURATION) {
            String[] parsed = configuration.split(":");

            ChessGame.TeamColor team = parsed[0].equals("W") ? ChessGame.TeamColor.WHITE : ChessGame.TeamColor.BLACK;
            ChessPiece.PieceType type = pieceRegistry.get(parsed[1]);
            ChessPosition position = algebraicToMatrix(parsed[2]);

            this.addPiece(position, new ChessPiece(team, type));
        }
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessBoard that = (ChessBoard) o;
        return Objects.deepEquals(board, that.board);
    }

    @Override
    public int hashCode() {
        return Arrays.deepHashCode(board);
    }
}
