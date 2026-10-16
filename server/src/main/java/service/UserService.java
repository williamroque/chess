package service;

import dataaccess.UserDAO;
import model.UserData;
import service.results.LoginResult;

public class UserService {
    final UserDAO userDAO;

    public UserService(UserDAO userDAO) {
        this.userDAO = userDAO;
    }

    LoginResult register(UserData user) {
        return new LoginResult(null, null);
    }

    LoginResult login(UserData user) {
        return new LoginResult(null, null);
    }

    void logout(String authToken) {

    }
}
