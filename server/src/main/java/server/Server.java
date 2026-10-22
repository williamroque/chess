package server;

import com.google.gson.Gson;
import dataaccess.DatabaseManager;
import dataaccess.exceptions.DataAccessException;
import dataaccess.memory.MemoryAppDAO;
import dataaccess.memory.MemoryGameDAO;
import dataaccess.memory.MemoryUserDAO;
import dataaccess.sql.SQLAppDAO;
import dataaccess.sql.SQLGameDAO;
import dataaccess.sql.SQLUserDAO;
import handler.AppHandler;
import handler.GameHandler;
import handler.UserHandler;
import io.javalin.*;
import io.javalin.http.Context;
import io.javalin.json.JavalinGson;
import service.AppService;
import service.GameService;
import service.UserService;

public class Server {

    private final Javalin javalin;

    public Server() {
        try {
            DatabaseManager.configureDatabase();
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }

        SQLUserDAO userDAO = new SQLUserDAO();
        SQLGameDAO gameDAO = new SQLGameDAO();
        SQLAppDAO appDAO = new SQLAppDAO(userDAO, gameDAO);

        UserService userService = new UserService(userDAO);
        GameService gameService = new GameService(gameDAO, userDAO);
        AppService appService = new AppService(appDAO);

        AppHandler appHandler = new AppHandler(appService, userService);
        UserHandler userHandler = new UserHandler(userService);
        GameHandler gameHandler = new GameHandler(gameService, userService);

        javalin = Javalin.create(config -> {
                    config.staticFiles.add("web");
                    config.jsonMapper(new JavalinGson());
                })
                .delete("/db", appHandler::clear)
                .post("/user", userHandler::register)
                .post("/session", userHandler::login)
                .delete("/session", userHandler::logout)
                .get("/game", gameHandler::listGames)
                .post("/game", gameHandler::createGame)
                .put("/game", gameHandler::joinGame);
    }

    public int run(int desiredPort) {
        javalin.start(desiredPort);
        return javalin.port();
    }

    public void stop() {
        javalin.stop();
    }
}
