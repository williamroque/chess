package dataaccess.sql;

import dataaccess.DatabaseManager;
import dataaccess.UserDAO;
import dataaccess.exceptions.AlreadyTakenException;
import dataaccess.exceptions.AuthTokenNotFoundException;
import dataaccess.exceptions.DataAccessException;
import model.AuthData;
import model.UserData;

import java.sql.ResultSet;
import java.util.Collection;
import java.util.List;

public class SQLUserDAO implements UserDAO {
    @Override
    public UserData getUser(String username) throws DataAccessException {
        String sql = "SELECT username, password, email FROM users WHERE username = ? LIMIT 1;";

        List<UserData> results = DatabaseManager.executeQuery(
                sql,
                rs -> new UserData(
                        rs.getString("username"),
                        rs.getString("password"),
                        rs.getString("email")
                ),
                username
        );

        return results.isEmpty() ? null : results.getFirst();
    }

    @Override
    public String getUsernameByToken(String authToken) throws DataAccessException {
        String sql = "SELECT username FROM auths WHERE authToken = ? LIMIT 1;";

        List<String> results = DatabaseManager.executeQuery(
                sql,
                rs -> rs.getString("username"),
                authToken
        );

        return results.isEmpty() ? null : results.getFirst();
    }

    @Override
    public void createUser(UserData user) throws DataAccessException {
        if (getUser(user.username()) != null) {
            throw new AlreadyTakenException("This username already exists!");
        }

        String sql = "INSERT INTO users (username, password, email) VALUES (?, ?, ?);";

        DatabaseManager.executeUpdate(sql, user.username(), user.password(), user.email());
    }

    @Override
    public void createAuth(AuthData auth) throws DataAccessException {
        String sql = "INSERT INTO auths (authToken, username) VALUE (?, ?)";

        DatabaseManager.executeUpdate(sql, auth.authToken(), auth.username());
    }

    @Override
    public boolean authExists(String authToken) throws DataAccessException {
        String sql = "SELECT * FROM auths WHERE authToken = ? LIMIT 1";

        Collection<ResultSet> results = DatabaseManager.executeQuery(sql, rc -> rc, authToken);

        return results.size() == 1;
    }

    @Override
    public void deleteAuth(String authToken) throws DataAccessException {
        if (!authExists(authToken)) {
            throw new AuthTokenNotFoundException("The provided username-token pair does not exist.");
        }

        String sql = "DELETE FROM auths WHERE authToken = ? LIMIT 1";

        DatabaseManager.executeUpdate(sql, authToken);
    }

    @Override
    public void clear() throws DataAccessException {
        DatabaseManager.executeUpdate("TRUNCATE TABLE users");
        DatabaseManager.executeUpdate("TRUNCATE TABLE auths");
    }

    public Collection<UserData> getUsers() throws DataAccessException {
        String sql = "SELECT username, password, email FROM users;";

        return DatabaseManager.executeQuery(
                sql,
                rs -> new UserData(
                        rs.getString("username"),
                        rs.getString("password"),
                        rs.getString("email")
                )
        );
    }

    public Collection<AuthData> getAuths() throws DataAccessException {
        String sql = "SELECT username, authToken FROM auths;";

        return DatabaseManager.executeQuery(
                sql,
                rs -> new AuthData(
                        rs.getString("username"),
                        rs.getString("authToken")
                )
        );
    }
}
