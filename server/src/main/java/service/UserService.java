package service;

import dataaccess.UserDAO;
import model.UserData;
import service.results.LoginResult;

public class UserService {
    final UserDAO userDAO;

    public UserService(UserDAO userDAO) {
        this.userDAO = userDAO;
    }

    public LoginResult register(UserData user) {
        return new LoginResult(null, null);
    }

    public LoginResult login(UserData user) {
        return new LoginResult(null, null);
    }

    public void logout(String authToken) {

    }
}
