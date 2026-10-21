package dataaccess.sql;

import dataaccess.UserDAO;
import dataaccess.exceptions.AlreadyTakenException;
import dataaccess.exceptions.AuthTokenNotFoundException;
import model.AuthData;
import model.UserData;

import java.sql.Connection;
import java.util.Collection;
import java.util.HashMap;

public class SQLUserDAO implements UserDAO {
    HashMap<String, UserData> users = new HashMap<>();
    HashMap<String, AuthData> auths = new HashMap<>();

    Connection connection;

    public SQLUserDAO(Connection connection) {
        this.connection = connection;
    }

    @Override
    public UserData getUser(String username) {
        return users.get(username);
    }

    @Override
    public String getUsernameByToken(String authToken) {
        return auths.get(authToken) == null ? null : auths.get(authToken).username();
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
    public boolean authExists(String authToken) {
        return auths.containsKey(authToken);
    }

    @Override
    public void deleteAuth(String authToken) throws AuthTokenNotFoundException {
        if (!authExists(authToken)) {
            throw new AuthTokenNotFoundException("The provided username-token pair does not exist.");
        }

        auths.remove(authToken);
    }

    @Override
    public void clear() {
        users.clear();
        auths.clear();
    }

    public Collection<UserData> getUsers() {
        return users.values();
    }

    public Collection<AuthData> getAuths() {
        return auths.values();
    }
}
