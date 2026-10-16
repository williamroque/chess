package dataaccess.memory;

import dataaccess.exceptions.AlreadyTakenException;
import dataaccess.exceptions.AuthTokenNotFoundException;
import dataaccess.UserDAO;
import model.AuthData;
import model.UserData;

import java.util.HashMap;

public class MemoryUserDAO implements UserDAO {
    HashMap<String, UserData> users = new HashMap<>();
    HashMap<String, AuthData> auths = new HashMap<>();

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
        auths.put(auth.authToken(), auth);
    }

    @Override
    public boolean authExists(AuthData auth) {
        return auths.containsKey(auth.authToken());
    }

    @Override
    public void deleteAuth(AuthData auth) throws AuthTokenNotFoundException {
        if (!authExists(auth)) {
            throw new AuthTokenNotFoundException("The provided username-token pair does not exist.");
        }

        auths.remove(auth.authToken());
    }

    @Override
    public void clear() {
        users.clear();
        auths.clear();
    }

    public HashMap<String, UserData> _getUsers() {
        return users;
    }

    public HashMap<String, AuthData> _getAuths() {
        return auths;
    }

    public void _setUsers(HashMap<String, UserData> users) {
        this.users = users;
    }

    public void _setAuths(HashMap<String, AuthData> auths) {
        this.auths = auths;
    }
}
