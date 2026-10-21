package dataaccess;

import dataaccess.exceptions.AlreadyTakenException;
import dataaccess.exceptions.AuthTokenNotFoundException;
import dataaccess.exceptions.DataAccessException;
import model.AuthData;
import model.UserData;

public interface UserDAO {
    UserData getUser(String username) throws DataAccessException;
    String getUsernameByToken(String username) throws DataAccessException;
    void createUser(UserData user) throws DataAccessException;

    void createAuth(AuthData auth) throws DataAccessException;
    boolean authExists(String authToken) throws DataAccessException;
    void deleteAuth(String authToken) throws DataAccessException;

    void clear();
}
