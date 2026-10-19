package dataaccess;

import chess.ChessGame;
import dataaccess.exceptions.GameNotFoundException;
import model.GameData;

import java.util.Collection;

public interface GameDAO {
    Collection<GameData> getGameList();
    GameData getGame(int gameID);

    GameData createGame(String gameName, ChessGame game);

    void updateGame(GameData game) throws GameNotFoundException;

    void clear();
}
