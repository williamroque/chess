package dataaccess.memory;

import dataaccess.AlreadyTakenException;
import dataaccess.AuthTokenNotFoundException;
import dataaccess.UserDAO;
import model.AuthData;
import model.UserData;

import java.util.HashMap;

public class MemoryUserDAO implements UserDAO {
    HashMap<String, UserData> users;
    HashMap<String, AuthData> auths;

    @Override
    public UserData getUser(String username) {
        return users.get(username);
    }

    @Override
    public void createUser(UserData user) throws AlreadyTakenException {
        if (users.containsKey(user.username())) {
            throw new AlreadyTakenException("This username already exists!");
        }

        users.put(user.username(), user);
    }

    @Override
    public void createAuth(AuthData auth) {
        auths.put(auth.username(), auth);
    }

    @Override
    public boolean authExists(AuthData auth) {
        AuthData storedAuth = auths.get(auth.username());

        if (storedAuth != null) {
            return storedAuth.authToken().equals(auth.authToken());
        }

        return false;
    }

    @Override
    public void deleteAuth(AuthData auth) throws AuthTokenNotFoundException {
        if (!authExists(auth)) {
            throw new AuthTokenNotFoundException("The provided username-token pair does not exist.");
        }

        auths.remove(auth.username());
    }

    @Override
    public void clear() {
        users.clear();
        auths.clear();
    }
}
