package chess;

import java.util.Collection;
import java.util.Map;
import java.util.Objects;

/**
 * A class that can manage a chess game, making moves on a board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessGame {
    private TeamColor teamTurn = TeamColor.WHITE;
    private ChessBoard board;

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

        return piece.pieceMoves(board, startPosition)
                .stream()
                .filter(move -> isMoveSafe(move, teamTurn))
                .toList();
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

    /**
     * Makes a move in the chess game
     *
     * @param move chess move to perform
     * @throws InvalidMoveException if move is invalid
     */
    public void makeMove(ChessMove move) throws InvalidMoveException {
        if (isMoveSafe(move, teamTurn)) {
            board.movePiece(move);
        } else {
            throw new InvalidMoveException();
        }
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
