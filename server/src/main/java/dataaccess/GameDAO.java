package dataaccess;

import chess.ChessGame;
import model.GameData;

import java.util.Collection;

public interface GameDAO {
    Collection<GameData> getGameList();
    GameData getGame(int gameID);

    int createGame(String gameName, ChessGame game);

    void updateGame(GameData game) throws GameNotFoundException;

    void clear();
}
