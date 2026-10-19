package service;

import chess.ChessGame;
import dataaccess.GameDAO;
import dataaccess.UserDAO;
import dataaccess.exceptions.AuthTokenNotFoundException;
import dataaccess.exceptions.InvalidGameNameException;
import model.GameData;

import java.util.*;

public class GameService {
    final GameDAO gameDAO;
    final UserDAO userDAO;

    public GameService(GameDAO gameDAO, UserDAO userDAO) {
        this.gameDAO = gameDAO;
        this.userDAO = userDAO;
    }

    public Collection<GameData> listGames(String authToken) throws AuthTokenNotFoundException {
        if (!userDAO.authExists(authToken)) {
            throw new AuthTokenNotFoundException("Auth token not found.");
        }

        return gameDAO.getGameList();
    }

    public GameData createGame(String authToken, String gameName) throws AuthTokenNotFoundException, InvalidGameNameException {
        if (!userDAO.authExists(authToken)) {
            throw new AuthTokenNotFoundException("Auth token not found.");
        }

        if (gameName.isEmpty()) {
            throw new InvalidGameNameException("Game name is invalid.");
        }

        ChessGame game = new ChessGame();

        return gameDAO.createGame(gameName, game);
    }

    public void joinGame(String authToken, int gameID, ChessGame.TeamColor teamColor) { }
}
