package java.passoff.service;

import dataaccess.AppDAO;
import dataaccess.GameDAO;
import dataaccess.UserDAO;
import dataaccess.memory.MemoryAppDAO;
import dataaccess.memory.MemoryGameDAO;
import dataaccess.memory.MemoryUserDAO;
import org.junit.jupiter.api.*;
import passoff.model.*;
import service.AppService;
import service.GameService;
import service.UserService;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class UserServiceTests {
    private static UserDAO userDAO;
    private static GameDAO gameDAO;
    private static AppDAO appDAO;

    private static UserService userService;
    private static GameService gameService;
    private static AppService appService;


    @BeforeAll
    public static void init() {
        MemoryUserDAO userDAO = new MemoryUserDAO();
        MemoryGameDAO gameDAO = new MemoryGameDAO();
        MemoryAppDAO appDAO = new MemoryAppDAO(userDAO, gameDAO);

        userService = new UserService(userDAO);
        gameService = new GameService(gameDAO);
        appService = new AppService(appDAO);
    }

    @BeforeEach
    public void setup() {
        appService.clear();
    }

    // ### SERVER-LEVEL API TESTS ###

    @Test
    @Order(1)
    @DisplayName("Test ")
    public void staticFilesSuccess() {
    }
}
