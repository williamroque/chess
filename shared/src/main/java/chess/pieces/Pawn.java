package chess.pieces;

import chess.ChessGame;
import chess.ChessPiece;

public class Pawn extends ChessPiece {
    public Pawn(ChessGame.TeamColor pieceColor) {
        super(pieceColor, PieceType.PAWN);
    }
}
