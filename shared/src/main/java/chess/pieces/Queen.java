package chess.pieces;

import chess.ChessGame;
import chess.ChessPiece;

public class Queen extends ChessPiece {
    public Queen(ChessGame.TeamColor pieceColor) {
        super(pieceColor, PieceType.QUEEN);
    }
}
