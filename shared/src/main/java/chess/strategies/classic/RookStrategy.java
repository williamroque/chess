package chess.strategies.classic;

import chess.*;

import java.util.ArrayList;
import java.util.List;

public class RookStrategy implements ChessStrategy {
    private final int[][] offsets = {
            {1, 0},
            {-1, 0},
            {0, -1},
            {0, 1}
    };

    @Override
    public List<ChessMove> getValidMoves(ChessPosition position, ChessBoard board, ChessGame.TeamColor team) {
        return getRayMoves(position, board, team, offsets);
    }
}
