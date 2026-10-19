package handler;

import io.javalin.http.Context;
import service.GameService;

public class GameHandler {
    GameService gameService;

    public GameHandler(GameService gameService) {
        this.gameService = gameService;
    }
}
