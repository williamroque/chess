package chess.strategies.classic;

import chess.*;

import java.util.ArrayList;
import java.util.List;

public class KnightStrategy implements ChessStrategy {
    private final int[][] offsets = {
            {2, 1},
            {2, -1},
            {1, 2},
            {1, -2},
            {-2, 1},
            {-2, -1},
            {-1, 2},
            {-1, -2}
    };

    @Override
    public List<ChessMove> getValidMoves(ChessPosition position, ChessBoard board, ChessGame.TeamColor team) {
        return getSimpleMoves(position, board, team, offsets);
    }
}
