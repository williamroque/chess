package chess.strategies.classic;

import chess.*;

import java.util.ArrayList;
import java.util.List;

public class KingStrategy implements ChessStrategy {
    private final int[][] offsets = {
            {0, -1},
            {0, 1},
            {1, 0},
            {-1, 0},
            {-1, -1},
            {1, 1},
            {-1, 1},
            {1, -1}
    };

    @Override
    public List<ChessMove> getValidMoves(ChessPosition position, ChessBoard board, ChessGame.TeamColor team) {
        ArrayList<ChessMove> validMoves = new ArrayList<>();

        int row = position.getRow();
        int col = position.getColumn();

        for (int[] offset : offsets) {
            ChessPosition positionCandidate = new ChessPosition(
                    row + offset[0],
                    col + offset[1]
            );

            if (!board.isValidPosition(positionCandidate)) continue;

            ChessPiece targetPiece = board.getPiece(positionCandidate);

            if (targetPiece == null || targetPiece.getTeamColor() != team) {
                validMoves.add(
                        new ChessMove(position, positionCandidate, null)
                );
            }
        }

        return validMoves;
    }
}
