package passoff.service;

import dataaccess.AppDAO;
import dataaccess.GameDAO;
import dataaccess.UserDAO;
import dataaccess.memory.MemoryAppDAO;
import dataaccess.memory.MemoryGameDAO;
import dataaccess.memory.MemoryUserDAO;
import model.AuthData;
import model.UserData;
import org.junit.jupiter.api.*;
import passoff.model.*;
import service.AppService;
import service.GameService;
import service.UserService;

import java.util.HashMap;
import java.util.Map;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class UserServiceTests {
    private static MemoryUserDAO userDAO;
    private static MemoryGameDAO gameDAO;
    private static MemoryAppDAO appDAO;

    private static UserService userService;
    private static GameService gameService;
    private static AppService appService;


    @BeforeAll
    public static void init() {
        userDAO = new MemoryUserDAO();
        gameDAO = new MemoryGameDAO();
        appDAO = new MemoryAppDAO(userDAO, gameDAO);

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
    @DisplayName("Test clearing in app service with users.")
    public void testAppClearsUsers() {
        HashMap<String, UserData> dummyUsers = new HashMap<>(Map.of(
                "johnny", new UserData("johnny", "1234", "johnny@email.com"),
                "megan", new UserData("megan", "4321", "megan@email.com"),
                "charlie", new UserData("charlie", "4444", "charlie@email.com")
        ));
        HashMap<String, AuthData> dummyAuths = new HashMap<>(Map.of(
                "1234-4312-1234-0175", new AuthData("1234-4312-1234-0175", "johnny"),
                "0982-3917-5916-5629", new AuthData("0982-3917-5916-5629", "megan"),
                "5017-0572-9572-4827", new AuthData("5017-0572-9572-4827", "charlie")
        ));

        userDAO._setUsers(dummyUsers);
        userDAO._setAuths(dummyAuths);

        Assertions.assertFalse(userDAO._getUsers().isEmpty());
        Assertions.assertFalse(userDAO._getAuths().isEmpty());

        appService.clear();

        Assertions.assertTrue(userDAO._getUsers().isEmpty());
        Assertions.assertTrue(userDAO._getAuths().isEmpty());
    }
}
