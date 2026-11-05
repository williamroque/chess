package dataaccess;

import chess.ChessGame;
import dataaccess.exceptions.DataAccessException;
import dataaccess.exceptions.GameNotFoundException;
import model.GameData;
import model.GameDataList;

import java.util.ArrayList;
import java.util.Collection;

public interface GameDAO {
    GameDataList getGameList() throws DataAccessException;

    GameData getGame(int gameID) throws DataAccessException;

    GameData createGame(String gameName, ChessGame game) throws DataAccessException;

    void updateGame(GameData game) throws DataAccessException;

    void clear() throws DataAccessException;

    void setGames(ArrayList<GameData> dummyGames) throws DataAccessException;
}
