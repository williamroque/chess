package chess;

import java.util.List;

public interface ChessStrategy {
    List<ChessMove> getValidMoves(ChessPosition position, ChessBoard board, ChessGame.TeamColor team);
}
