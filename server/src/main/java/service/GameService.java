package service;

import chess.ChessGame;
import dataaccess.GameDAO;
import dataaccess.UserDAO;
import dataaccess.exceptions.*;
import model.GameData;

import java.util.*;

public class GameService {
    final GameDAO gameDAO;
    final UserDAO userDAO;

    public GameService(GameDAO gameDAO, UserDAO userDAO) {
        this.gameDAO = gameDAO;
        this.userDAO = userDAO;
    }

    public Collection<GameData> listGames(String authToken) throws DataAccessException {
        if (!userDAO.authExists(authToken)) {
            throw new AuthTokenNotFoundException("Auth token not found.");
        }

        return gameDAO.getGameList();
    }

    public GameData createGame(String authToken, String gameName) throws DataAccessException, InvalidGameNameException {
        if (!userDAO.authExists(authToken)) {
            throw new AuthTokenNotFoundException("Auth token not found.");
        }

        if (gameName.isBlank()) {
            throw new InvalidGameNameException("Game name is invalid.");
        }

        ChessGame game = new ChessGame();

        return gameDAO.createGame(gameName, game);
    }

    public void joinGame(String authToken, int gameID, ChessGame.TeamColor teamColor)
            throws DataAccessException {

        if (!userDAO.authExists(authToken)) {
            throw new AuthTokenNotFoundException("Auth token not found.");
        }

        GameData game = gameDAO.getGame(gameID);

        if (game == null) {
            throw new GameNotFoundException("Game with provided gameID not found.");
        } else if ((teamColor == ChessGame.TeamColor.WHITE && game.whiteUsername() != null)
                || (teamColor == ChessGame.TeamColor.BLACK && game.blackUsername() != null)) {
            throw new AlreadyTakenException("Someone is already playing for this team in the game.");
        }

        String username = userDAO.getUsernameByToken(authToken);

        GameData updatedGame = new GameData(
                game.gameID(),
                teamColor == ChessGame.TeamColor.WHITE ? username : game.whiteUsername(),
                teamColor == ChessGame.TeamColor.BLACK ? username : game.blackUsername(),
                game.gameName(),
                game.game()
        );

        gameDAO.updateGame(updatedGame);
    }
}
