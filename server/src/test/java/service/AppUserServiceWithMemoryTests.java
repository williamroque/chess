package service;

import dataaccess.DatabaseManager;
import dataaccess.memory.MemoryAppDAO;
import dataaccess.memory.MemoryGameDAO;
import dataaccess.memory.MemoryUserDAO;
import shared.AppUserServiceTests;
import org.junit.jupiter.api.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class AppUserServiceWithMemoryTests extends AppUserServiceTests {
    @BeforeAll
    public static void init() {
        try {
            DatabaseManager.configureDatabase();
        } catch (Exception e) {
            System.err.println(e);
        }

        userDAO = new MemoryUserDAO();
        gameDAO = new MemoryGameDAO();
        appDAO = new MemoryAppDAO((MemoryUserDAO) userDAO, (MemoryGameDAO) gameDAO);

        userService = new UserService(userDAO);
        gameService = new GameService(gameDAO, userDAO);
        appService = new AppService(appDAO);
    }
}
