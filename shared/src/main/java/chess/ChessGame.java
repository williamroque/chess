package chess;

import java.lang.reflect.Array;
import java.util.*;

/**
 * A class that can manage a chess game, making moves on a board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessGame {
    private TeamColor teamTurn = TeamColor.WHITE;
    private ChessBoard board;

    private enum GameFlags {
        DOUBLE_FORWARD,
        WHITE_KING_MOVED,
        BLACK_KING_MOVED;
    }

    private EnumSet<GameFlags> currentFlags = EnumSet.noneOf(GameFlags.class);
    ChessPosition enPassantCandidate;

    public ChessGame() {
        board = new ChessBoard();
        board.resetBoard();
    }

    /**
     * @return Which team's turn it is
     */
    public TeamColor getTeamTurn() {
        return teamTurn;
    }

    /**
     * Sets which teams turn it is
     *
     * @param team the team whose turn it is
     */
    public void setTeamTurn(TeamColor team) {
        teamTurn = team;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessGame chessGame = (ChessGame) o;
        return teamTurn == chessGame.teamTurn && Objects.equals(board, chessGame.board);
    }

    @Override
    public int hashCode() {
        return Objects.hash(teamTurn, board);
    }

    /**
     * Enum identifying the 2 possible teams in a chess game
     */
    public enum TeamColor {
        WHITE,
        BLACK
    }

    private ChessMove getEnPassantMove(ChessPiece piece, ChessPosition position) {
        boolean isPawn = piece.getPieceType() == ChessPiece.PieceType.PAWN;
        boolean wasDoubleForward = currentFlags.contains(GameFlags.DOUBLE_FORWARD);

        if (isPawn && wasDoubleForward && enPassantCandidate != null) {
            boolean sameRow = position.getRow() == enPassantCandidate.getRow();
            boolean isNeighbor = Math.abs(enPassantCandidate.getColumn() - position.getColumn()) == 1;

            if (sameRow && isNeighbor) {
                int direction = piece.getTeamColor() == TeamColor.WHITE ? 1 : -1;

                ChessPosition enPassantPosition = new ChessPosition(
                        position.getRow() + direction,
                        enPassantCandidate.getColumn()
                );

                return new ChessMove(position, enPassantPosition, null);
            }
        }

        return null;
    }

    private boolean wasEnPassant(ChessMove move) {
        ChessPiece piece = board.getPiece(move.getEndPosition());

        if (piece != null && piece.getPieceType() == ChessPiece.PieceType.PAWN) {
            int startColumn = move.getStartPosition().getColumn();
            int endColumn = move.getEndPosition().getColumn();

            return startColumn != endColumn;
        }

        return false;
    }

    private ChessMove getCastlingMove(ChessPiece piece, ChessPosition position) {
        return null;
    }

    /**
     * Gets all valid moves for a piece at the given location
     *
     * @param startPosition the piece to get valid moves for
     * @return Set of valid moves for requested piece, or null if no piece at
     * startPosition
     */
    public Collection<ChessMove> validMoves(ChessPosition startPosition) {
        ChessPiece piece = board.getPiece(startPosition);

        if (piece == null) return null;

        ArrayList<ChessMove> moves = new ArrayList<ChessMove>(piece.pieceMoves(board, startPosition)
                .stream()
                .filter(move -> isMoveSafe(move, piece.getTeamColor()))
                .toList());

        ChessMove enPassantMove = getEnPassantMove(piece, startPosition);

        if (enPassantMove != null) {
            moves.add(enPassantMove);
        }

        return moves;
    }

    private boolean isMoveSafe(ChessMove move, TeamColor team) {
        ChessPiece targetPiece = board.getPiece(move.getEndPosition());
        board.movePiece(move);

        boolean isSafe = !isInCheck(team);

        board.movePiece(
                new ChessMove(
                        move.getEndPosition(),
                        move.getStartPosition(),
                        null
                )
        );
        board.addPiece(move.getEndPosition(), targetPiece);

        return isSafe;
    }

    private void setFlags(ChessPiece piece, ChessMove move) {
        currentFlags.remove(GameFlags.DOUBLE_FORWARD);

        switch (piece.getPieceType()) {
            case PAWN:
                int moveDistance = Math.abs(
                        move.getEndPosition().getRow() - move.getStartPosition().getRow()
                );

                if (moveDistance == 2) {
                    currentFlags.add(GameFlags.DOUBLE_FORWARD);
                }

                enPassantCandidate = move.getEndPosition();

                break;
            case KING:
                if (piece.getTeamColor() == TeamColor.WHITE) {
                    currentFlags.add(GameFlags.WHITE_KING_MOVED);
                } else {
                    currentFlags.add(GameFlags.BLACK_KING_MOVED);
                }
                break;
        };
    }

    /**
     * Makes a move in the chess game
     *
     * @param move chess move to perform
     * @throws InvalidMoveException if move is invalid
     */
    public void makeMove(ChessMove move) throws InvalidMoveException {
        ChessPiece piece = board.getPiece(move.getStartPosition());

        boolean moveChecks = (
                piece != null
                        && piece.getTeamColor() == teamTurn
                        && validMoves(move.getStartPosition()).contains(move)
        );

        if (moveChecks) {
            board.movePiece(move);

            if (wasEnPassant(move)) {
                board.removePiece(enPassantCandidate);
            }

            ChessPiece.PieceType promotionPiece = move.getPromotionPiece();

            if (promotionPiece != null) {
                ChessPiece newPiece = new ChessPiece(teamTurn, promotionPiece);
                board.addPiece(move.getEndPosition(), newPiece);
            }

            setFlags(piece, move);
        } else {
            throw new InvalidMoveException();
        }

        teamTurn = teamTurn == TeamColor.WHITE ? TeamColor.BLACK : TeamColor.WHITE;
    }

    /**
     * Determines if the given team is in check
     *
     * @param teamColor which team to check for check
     * @return True if the specified team is in check
     */
    public boolean isInCheck(TeamColor teamColor) {
        ChessPosition kingPosition = board.find(ChessPiece.PieceType.KING, teamColor);
        TeamColor opposingTeam = teamColor == TeamColor.WHITE ? TeamColor.BLACK : TeamColor.WHITE;

        for (ChessPosition position : board.getTeamPositions(opposingTeam)) {
            ChessPiece piece = board.getPiece(position);

            ChessMove move = new ChessMove(position, kingPosition, null);

            if (piece.isValidMove(board, move)) {
                return true;
            }
        }

        return false;
    }

    /**
     * Determines if the given team is in checkmate
     *
     * @param teamColor which team to check for checkmate
     * @return True if the specified team is in checkmate
     */
    public boolean isInCheckmate(TeamColor teamColor) {
        return isInCheck(teamColor) && board.getTeamPositions(teamColor)
                .stream()
                .allMatch(p -> board.getPiece(p)
                        .pieceMoves(board, p)
                        .stream()
                        .noneMatch(m -> isMoveSafe(m, teamColor)));
    }

    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves while not in check.
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor) {
        return !isInCheck(teamColor) && board.getTeamPositions(teamColor)
                .stream()
                .allMatch(p -> board.getPiece(p)
                        .pieceMoves(board, p)
                        .stream()
                        .noneMatch(m -> isMoveSafe(m, teamColor)));
    }

    /**
     * Sets this game's chessboard to a given board
     *
     * @param board the new board to use
     */
    public void setBoard(ChessBoard board) {
        this.board = board;
    }

    /**
     * Gets the current chessboard
     *
     * @return the chessboard
     */
    public ChessBoard getBoard() {
        return board;
    }
}
