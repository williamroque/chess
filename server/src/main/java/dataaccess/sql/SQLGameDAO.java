package dataaccess.sql;

import chess.ChessGame;
import com.google.gson.Gson;
import dataaccess.DatabaseManager;
import dataaccess.GameDAO;
import dataaccess.exceptions.DataAccessException;
import dataaccess.exceptions.GameNotFoundException;
import model.GameData;

import java.util.Collection;

public class SQLGameDAO implements GameDAO {
    @Override
    public Collection<GameData> getGameList() throws DataAccessException {
        String sql = "SELECT * FROM games";

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
        });
    }

    @Override
    public GameData getGame(int gameID) {
//        return games.get(gameID);

        return new GameData(0, null, null, null, null);
    }

    @Override
    public GameData createGame(String gameName, ChessGame game) {
//        int gameID = games.size() + 1;
//
//        GameData data = new GameData(
//                gameID,
//                null,
//                null,
//                gameName,
//                game
//        );
//
//        games.put(gameID, data);
//
//        return data;

        return new GameData(0, null, null, null, null);
    }

    @Override
    public void updateGame(GameData game) throws GameNotFoundException {
//        if (getGame(game.gameID()) == null) {
//            throw new GameNotFoundException("Game with the specified ID does not exist.");
//        }
//
//        games.put(game.gameID(), game);
    }

    @Override
    public void clear() {
//        games.clear();
    }

    public void setGames(Collection<GameData> gameList) {
//        for (GameData game : gameList) {
//            int gameID = games.size() + 1;
//            games.put(gameID, game);
//        }
    }
}
