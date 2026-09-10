package chess.strategies.classic;

import chess.*;

import java.util.ArrayList;
import java.util.List;

public class BishopStrategy implements ChessStrategy {
    private final int[][] offsets = {
            {-1, -1},
            {-1, 1},
            {1, -1},
            {1, 1}
    };

    @Override
    public List<ChessMove> getValidMoves(ChessPosition position, ChessBoard board, ChessGame.TeamColor team) {
        ArrayList<ChessMove> validMoves = new ArrayList<>();

        int row = position.getRow();
        int col = position.getColumn();

        for (int[] offset : offsets) {
            int scalar = 1;
            ChessPosition positionCandidate = new ChessPosition(
                    row + offset[0],
                    col + offset[1]
            );

            while (board.isValidPosition(positionCandidate)) {
                ChessPiece targetPiece = board.getPiece(positionCandidate);

                if (targetPiece == null || targetPiece.getTeamColor() != team) {
                    validMoves.add(
                            new ChessMove(position, positionCandidate, null)
                    );
                }

                if (targetPiece != null) break;

                scalar++;
                positionCandidate = new ChessPosition(
                        row + offset[0] * scalar,
                        col + offset[1] * scalar
                );
            }
        }

        return validMoves;
    }
}
