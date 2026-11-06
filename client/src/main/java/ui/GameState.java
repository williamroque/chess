package ui;

import chess.ChessGame;
import model.GameData;

public class GameState {
    GameData currentGame;
    ChessGame.TeamColor currentTeam;

    public GameState() {}

    public GameData getGame() {
        return currentGame;
    }

    public ChessGame.TeamColor getTeam() {
        return currentTeam;
    }

    public void setGame(GameData game) {
        currentGame = game;
    }

    public void setTeam(ChessGame.TeamColor game) {
        currentTeam = game;
    }

    public void clearGame() {
        currentGame = null;
    }

    public void clearTeam() {
        currentTeam = null;
    }
}
