package chess.strategies.classic;

import chess.*;

import java.util.ArrayList;
import java.util.List;

public class PawnStrategy implements ChessStrategy {
    private List<ChessMove> getPossiblePromotions(ChessPosition position, ChessPosition positionCandidate) {
        ArrayList<ChessMove> validMoves = new ArrayList<>();

        if (positionCandidate.getRow() == 8 || positionCandidate.getRow() == 1) {
            validMoves.add(new ChessMove(position, positionCandidate, ChessPiece.PieceType.BISHOP));
            validMoves.add(new ChessMove(position, positionCandidate, ChessPiece.PieceType.ROOK));
            validMoves.add(new ChessMove(position, positionCandidate, ChessPiece.PieceType.KNIGHT));
            validMoves.add(new ChessMove(position, positionCandidate, ChessPiece.PieceType.QUEEN));
        } else {
            validMoves.add(new ChessMove(position, positionCandidate, null));
        }

        return validMoves;
    }

    private boolean canAttack(ChessPosition attackPosition, ChessBoard board, ChessGame.TeamColor team) {
        boolean attackExists = board.isValidPosition(attackPosition);
        boolean attackNotEmpty = attackExists && board.getPiece(attackPosition) != null;

        return attackNotEmpty && board.getPiece(attackPosition).getTeamColor() != team;
    }

    @Override
    public List<ChessMove> getValidMoves(ChessPosition position, ChessBoard board, ChessGame.TeamColor team) {
        ArrayList<ChessMove> validMoves = new ArrayList<>();

        int row = position.getRow();
        int col = position.getColumn();

        int direction = team == ChessGame.TeamColor.WHITE ? 1 : -1;

        ChessPosition forwardPosition = new ChessPosition(
                row + direction,
                col
        );
        ChessPosition doubleForwardPosition = new ChessPosition(
                row + direction * 2,
                col
        );
        ChessPosition leftAttackPosition = new ChessPosition(
                row + direction,
                col - 1
        );
        ChessPosition rightAttackPosition = new ChessPosition(
                row + direction,
                col + 1
        );

        if (board.isValidPosition(forwardPosition) && board.getPiece(forwardPosition) == null) {
            validMoves.addAll(getPossiblePromotions(position, forwardPosition));
        }

        boolean isStartingPosition = (
                team == ChessGame.TeamColor.WHITE && row == 2
                        || team == ChessGame.TeamColor.BLACK && row == 7
        );
        boolean canMoveDoubleForward = isStartingPosition
                && board.getPiece(forwardPosition) == null
                && board.getPiece(doubleForwardPosition) == null;

        if (canMoveDoubleForward) {
            validMoves.add(new ChessMove(position, doubleForwardPosition, null));
        }

        if (canAttack(leftAttackPosition, board, team)) {
            validMoves.addAll(getPossiblePromotions(position, leftAttackPosition));
        }

        if (canAttack(rightAttackPosition, board, team)) {
            validMoves.addAll(getPossiblePromotions(position, rightAttackPosition));
        }

        return validMoves;
    }
}
