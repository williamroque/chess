package dataaccess;

import model.AuthData;
import model.UserData;

import java.util.HashMap;

public interface UserDAO {
    UserData getUser(String username);
    void createUser(UserData user) throws AlreadyTakenException;

    void createAuth(AuthData auth);
    boolean authExists(AuthData auth);
    void deleteAuth(AuthData auth) throws AuthTokenNotFoundException;

    void clear();
}
