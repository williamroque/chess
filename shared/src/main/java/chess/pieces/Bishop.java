package chess.pieces;

import chess.ChessGame;
import chess.ChessPiece;

public class Bishop extends ChessPiece {
    public Bishop(ChessGame.TeamColor pieceColor) {
        super(pieceColor, PieceType.BISHOP, "B");
    }
}
