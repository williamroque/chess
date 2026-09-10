package chess;

import chess.pieces.Bishop;
import chess.strategies.classic.BishopStrategy;
import chess.strategies.classic.KingStrategy;
import chess.strategies.classic.KnightStrategy;
import chess.strategies.classic.PawnStrategy;

import java.util.Collection;
import java.util.Map;
import java.util.Objects;

/**
 * Represents a single chess piece
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessPiece {
    private final ChessGame.TeamColor pieceColor;
    private final ChessPiece.PieceType type;
    private final String shorthand;

    private final Map<ChessPiece.PieceType, ChessStrategy> strategyRegistry = Map.of(
            PieceType.BISHOP, new BishopStrategy(),
            PieceType.KING, new KingStrategy(),
            PieceType.KNIGHT, new KnightStrategy(),
            PieceType.PAWN, new PawnStrategy()
    );

    public ChessPiece(ChessGame.TeamColor pieceColor, ChessPiece.PieceType type) {
        this.pieceColor = pieceColor;
        this.type = type;
        this.shorthand = String.valueOf(type.name().charAt(0));
    }

    public ChessPiece(ChessGame.TeamColor pieceColor, ChessPiece.PieceType type, String shorthand) {
        this.pieceColor = pieceColor;
        this.type = type;
        this.shorthand = shorthand;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (!(o instanceof ChessPiece)) {
            return false;
        }

        ChessPiece that = (ChessPiece) o;
        return pieceColor == that.pieceColor && type == that.type;
    }

    @Override
    public int hashCode() {
        return Objects.hash(pieceColor, type);
    }

    @Override
    public String toString() {
        return "ChessPiece{" +
                "pieceColor=" + pieceColor +
                ", type=" + type +
                '}';
    }

    /**
     * The various different chess piece options
     */
    public enum PieceType {
        KING,
        QUEEN,
        BISHOP,
        KNIGHT,
        ROOK,
        PAWN
    }

    /**
     * @return Which team this chess piece belongs to
     */
    public ChessGame.TeamColor getTeamColor() {
        return this.pieceColor;
    }

    /**
     * @return which type of chess piece this piece is
     */
    public PieceType getPieceType() {
        return this.type;
    }

    /**
     * @return the piece shorthand notation
     */
    public String getShorthand() {
        return this.shorthand;
    }

    /**
     * Calculates all the positions a chess piece can move to
     * Does not take into account moves that are illegal due to leaving the king in
     * danger
     *
     * @return Collection of valid moves
     */
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        ChessStrategy strategy = strategyRegistry.get(this.type);
        return strategy.getValidMoves(myPosition, board, pieceColor);
    }
}
