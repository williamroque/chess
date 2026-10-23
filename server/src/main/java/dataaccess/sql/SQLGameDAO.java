package dataaccess.sql;

import chess.ChessGame;
import com.google.gson.Gson;
import dataaccess.DatabaseManager;
import dataaccess.GameDAO;
import dataaccess.exceptions.DataAccessException;
import dataaccess.exceptions.GameNotFoundException;
import model.GameData;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class SQLGameDAO implements GameDAO {
    private Collection<GameData> executeReturnGames(String sql, Object... params) throws DataAccessException {
        return DatabaseManager.executeQuery(sql, rs -> {
            String gameJSON = rs.getString("gameJSON");

            ChessGame game = new Gson().fromJson(gameJSON, ChessGame.class);

            return new GameData(
                    rs.getInt("id"),
                    rs.getString("whiteUsername"),
                    rs.getString("blackUsername"),
                    rs.getString("gameName"),
                    game
            );
        }, params);
    }

    @Override
    public Collection<GameData> getGameList() throws DataAccessException {
        return executeReturnGames("SELECT * FROM games");
    }

    @Override
    public GameData getGame(int gameID) throws DataAccessException {
        String sql = "SELECT * FROM games WHERE id = ? LIMIT 1";
        List<GameData> results = executeReturnGames(sql, gameID).stream().toList();

        return results.size() == 1 ? results.getFirst() : null;
    }

    @Override
    public GameData createGame(String gameName, ChessGame game) throws DataAccessException {
        String sql = "INSERT INTO games (gameName, gameJSON) VALUES (?, ?)";

        String gameJSON = new Gson().toJson(game);

        List<Integer> returnedIDs = DatabaseManager.executeUpdate(sql, gameName, gameJSON);

        if (returnedIDs.isEmpty()) {
            return null;
        }

        return new GameData(
                returnedIDs.getFirst(),
                null,
                null,
                gameName,
                game
        );
    }

    @Override
    public void updateGame(GameData game) throws DataAccessException {
        if (getGame(game.gameID()) == null) {
            throw new GameNotFoundException("Game with the specified ID does not exist.");
        }

        String sql = "UPDATE games SET whiteUsername = ?, blackUsername = ?, gameName = ?, gameJSON = ? WHERE id = ?";

        DatabaseManager.executeUpdate(
                sql,
                game.whiteUsername(),
                game.blackUsername(),
                game.gameName(),
                new Gson().toJson(game.game()),
                game.gameID()
        );
    }

    @Override
    public void clear() throws DataAccessException {
        DatabaseManager.executeUpdate("TRUNCATE TABLE games");
    }

    public void setGames(ArrayList<GameData> gameList) throws DataAccessException {
        for (GameData game : gameList) {
            createGame(game.gameName(), game.game());
            updateGame(game);
        }
    }
}
