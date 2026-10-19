package handler;

import io.javalin.http.Context;
import service.UserService;

public class UserHandler {
    UserService userService;

    public UserHandler(UserService userService) {
        this.userService = userService;
    }
}
