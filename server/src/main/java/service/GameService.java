package service;

import chess.ChessGame;
import dataaccess.GameDAO;
import model.GameData;

import java.util.*;

public class GameService {
    final GameDAO gameDAO;

    public GameService(GameDAO gameDAO) {
        this.gameDAO = gameDAO;
    }

    public Collection<GameData> listGames(String authToken) {
        return new ArrayList<>();
    }

    public GameData createGame(String authToken, String gameName) {
        return new GameData(0, null, null, null, null);
    }

    public void joinGame(String authToken, int gameID, ChessGame.TeamColor teamColor) { }
}
