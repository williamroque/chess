package service;

import java.util.UUID;

import dataaccess.exceptions.AlreadyTakenException;
import dataaccess.UserDAO;
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

    public LoginResult register(UserData user) throws AlreadyTakenException {
        if (userDAO.getUser(user.username()) != null) {
            throw new AlreadyTakenException("This username already exists!");
        }

        userDAO.createUser(user);

        AuthData auth = new AuthData(generateToken(), user.username());

        userDAO.createAuth(auth);

        return new LoginResult(user.username(), auth.authToken());
    }

    public LoginResult login(UserData user) throws InvalidCredentialsException {
        UserData dbUser = userDAO.getUser(user.username());

        if (dbUser == null || !dbUser.password().equals(user.password())) {
            throw new InvalidCredentialsException("Invalid login credentials.");
        }

        AuthData auth = new AuthData(generateToken(), user.username());

        userDAO.createAuth(auth);

        return new LoginResult(user.username(), auth.authToken());
    }

    public void logout(String authToken) {

    }
}
