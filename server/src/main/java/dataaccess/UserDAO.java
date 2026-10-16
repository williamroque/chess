package dataaccess;

import dataaccess.exceptions.AlreadyTakenException;
import dataaccess.exceptions.AuthTokenNotFoundException;
import model.AuthData;
import model.UserData;

public interface UserDAO {
    UserData getUser(String username);
    void createUser(UserData user) throws AlreadyTakenException;

    void createAuth(AuthData auth);
    boolean authExists(String authToken);
    void deleteAuth(String authToken) throws AuthTokenNotFoundException;

    void clear();
}
