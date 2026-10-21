package dataaccess.sql;

import chess.ChessGame;
import dataaccess.GameDAO;
import dataaccess.exceptions.GameNotFoundException;
import model.GameData;

import java.util.Collection;
import java.util.HashMap;

public class SQLGameDAO implements GameDAO {
    HashMap<Integer, GameData> games = new HashMap<>();

    @Override
    public Collection<GameData> getGameList() {
        return games.values();
    }

    @Override
    public GameData getGame(int gameID) {
        return games.get(gameID);
    }

    @Override
    public GameData createGame(String gameName, ChessGame game) {
        int gameID = games.size() + 1;

        GameData data = new GameData(
                gameID,
                null,
                null,
                gameName,
                game
        );

        games.put(gameID, data);

        return data;
    }

    @Override
    public void updateGame(GameData game) throws GameNotFoundException {
        if (getGame(game.gameID()) == null) {
            throw new GameNotFoundException("Game with the specified ID does not exist.");
        }

        games.put(game.gameID(), game);
    }

    @Override
    public void clear() {
        games.clear();
    }

    public void setGames(Collection<GameData> gameList) {
        for (GameData game : gameList) {
            int gameID = games.size() + 1;
            games.put(gameID, game);
        }
    }
}
