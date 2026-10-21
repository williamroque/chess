package service;

import java.util.UUID;

import dataaccess.exceptions.AlreadyTakenException;
import dataaccess.UserDAO;
import dataaccess.exceptions.AuthTokenNotFoundException;
import dataaccess.exceptions.DataAccessException;
import dataaccess.exceptions.InvalidCredentialsException;
import model.AuthData;
import model.UserData;
import service.results.LoginResult;

public class UserService {
    final UserDAO userDAO;

    private static String generateToken() {
        return UUID.randomUUID().toString();
    }

    public UserService(UserDAO userDAO) {
        this.userDAO = userDAO;
    }

    public LoginResult register(UserData user) throws DataAccessException {
        if (user.username() == null || user.username().isBlank()
                || user.password() == null || user.password().isBlank()
                || user.email() == null || user.email().isBlank()) {
            throw new InvalidCredentialsException("At least one of the provided credentials is invalid.");
        }

        if (userDAO.getUser(user.username()) != null) {
            throw new AlreadyTakenException("This username already exists!");
        }

        userDAO.createUser(user);

        AuthData auth = new AuthData(generateToken(), user.username());

        userDAO.createAuth(auth);

        return new LoginResult(user.username(), auth.authToken());
    }

    public LoginResult login(UserData user) throws DataAccessException {
        UserData dbUser = userDAO.getUser(user.username());

        if (dbUser == null || !dbUser.password().equals(user.password())) {
            throw new InvalidCredentialsException("Invalid login credentials.");
        }

        AuthData auth = new AuthData(generateToken(), user.username());

        userDAO.createAuth(auth);

        return new LoginResult(user.username(), auth.authToken());
    }

    public void logout(String authToken) throws DataAccessException {
        if (!userDAO.authExists(authToken)) {
            throw new AuthTokenNotFoundException("Auth token not found or invalid.");
        }

        userDAO.deleteAuth(authToken);
    }
}
