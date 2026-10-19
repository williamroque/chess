package server;

import dataaccess.memory.MemoryAppDAO;
import dataaccess.memory.MemoryGameDAO;
import dataaccess.memory.MemoryUserDAO;
import handler.AppHandler;
import handler.GameHandler;
import handler.UserHandler;
import io.javalin.*;
import io.javalin.json.JavalinGson;
import service.AppService;
import service.GameService;
import service.UserService;

public class Server {

    private final Javalin javalin;

    public Server() {
        MemoryUserDAO userDAO = new MemoryUserDAO();
        MemoryGameDAO gameDAO = new MemoryGameDAO();
        MemoryAppDAO appDAO = new MemoryAppDAO(userDAO, gameDAO);

        UserService userService = new UserService(userDAO);
        GameService gameService = new GameService(gameDAO, userDAO);
        AppService appService = new AppService(appDAO);

        AppHandler appHandler = new AppHandler(appService);
        UserHandler userHandler = new UserHandler(userService);
        GameHandler gameHandler = new GameHandler(gameService);

        javalin = Javalin.create(config -> {
                    config.staticFiles.add("web");
                    config.jsonMapper(new JavalinGson());
                })
                .delete("/db", appHandler::clear)
                .start(8080);
    }

    public int run(int desiredPort) {
        javalin.start(desiredPort);
        return javalin.port();
    }

    public void stop() {
        javalin.stop();
    }
}
