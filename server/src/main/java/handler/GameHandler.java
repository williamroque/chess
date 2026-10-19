package handler;

import io.javalin.http.Context;
import service.GameService;
import service.UserService;

public class GameHandler extends Handler {
    GameService gameService;

    public GameHandler(GameService gameService, UserService userService) {
        super(userService);
        this.gameService = gameService;
    }
}
