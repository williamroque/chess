package passoff.service;

import dataaccess.exceptions.AlreadyTakenException;
import dataaccess.exceptions.InvalidCredentialsException;
import dataaccess.memory.MemoryAppDAO;
import dataaccess.memory.MemoryGameDAO;
import dataaccess.memory.MemoryUserDAO;
import model.AuthData;
import model.UserData;
import org.junit.jupiter.api.*;
import service.AppService;
import service.GameService;
import service.UserService;
import service.results.LoginResult;

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

    private void populateDummyUsers() {
        HashMap<String, UserData> dummyUsers = new HashMap<>(Map.of(
                "johnny", new UserData("johnny", "1234", "johnny@email.com"),
                "megan", new UserData("megan", "4321", "megan@email.com"),
                "charlie", new UserData("charlie", "4444", "charlie@email.com")
        ));
        userDAO._setUsers(dummyUsers);
    }

    private void populateDummyAuths() {
        HashMap<String, AuthData> dummyAuths = new HashMap<>(Map.of(
                "1234-4312-1234-0175", new AuthData("1234-4312-1234-0175", "johnny"),
                "0982-3917-5916-5629", new AuthData("0982-3917-5916-5629", "megan"),
                "5017-0572-9572-4827", new AuthData("5017-0572-9572-4827", "charlie")
        ));
        userDAO._setAuths(dummyAuths);
    }

    /*
    * ==== Foundational Clear Test ====
    * */

    @Test
    @Order(1)
    @DisplayName("Test clearing in app service with users.")
    public void testAppClearsUsers() {
        populateDummyUsers();
        populateDummyAuths();

        Assertions.assertFalse(userDAO._getUsers().isEmpty());
        Assertions.assertFalse(userDAO._getAuths().isEmpty());

        appService.clear();

        Assertions.assertTrue(userDAO._getUsers().isEmpty());
        Assertions.assertTrue(userDAO._getAuths().isEmpty());
    }

    /*
     * ==== User Registration Tests ====
     * */

    @Test
    @Order(2)
    @DisplayName("Test registering new user to see if actually created")
    public void testRegisteringCreatesUsers() {
        Assertions.assertTrue(userDAO._getUsers().isEmpty());

        UserData dummyUser = new UserData("johnny", "1234", "johnny@email.com");

        try {
            userService.register(dummyUser);
        } catch (Exception _) {

        }

        Assertions.assertEquals(1, userDAO._getUsers().size());
        Assertions.assertEquals(dummyUser, userDAO._getUsers().values().toArray()[0]);
    }

    @Test
    @Order(3)
    @DisplayName("Test registering new user to see if auth created")
    public void testRegisteringCreatesAuth() {
        Assertions.assertTrue(userDAO._getAuths().isEmpty());

        UserData dummyUser = new UserData("johnny", "1234", "johnny@email.com");

        try {
            userService.register(dummyUser);
        } catch (Exception _) {

        }

        Assertions.assertEquals(1, userDAO._getAuths().size());
    }

    @Test
    @Order(4)
    @DisplayName("Test user already exists exception during registration")
    public void testRegisteringThrowsAlreadyExists() {
        Assertions.assertTrue(userDAO._getUsers().isEmpty());

        UserData dummyUser = new UserData("johnny", "1234", "johnny@email.com");

        try {
            userService.register(dummyUser);
        } catch (Exception _) {

        }

        Assertions.assertThrows(AlreadyTakenException.class, () -> {
            userService.register(dummyUser);
        });
    }

    @Test
    @Order(5)
    @DisplayName("Test if registering return reasonable login results")
    public void testRegisteringReturnsReasonableResults() {
        Assertions.assertTrue(userDAO._getUsers().isEmpty());

        UserData dummyUser = new UserData("johnny", "1234", "johnny@email.com");

        LoginResult result = new LoginResult(null, null);

        try {
            result = userService.register(dummyUser);
        } catch (Exception _) {

        }

        Assertions.assertEquals(dummyUser.username(), result.username());
        Assertions.assertFalse(result.authToken().isBlank());
    }

    /*
     * ==== User Login Tests ====
     * */

    @Test
    @Order(6)
    @DisplayName("Test if trying to log in with non-existent user throws exception")
    public void testLoginNonExistentUserThrowsException() {
        Assertions.assertTrue(userDAO._getUsers().isEmpty());

        UserData dummyUser = new UserData("johnny", "1234", "johnny@email.com");

        Assertions.assertThrows(InvalidCredentialsException.class, () -> {
            userService.login(dummyUser);
        });
    }

    @Test
    @Order(7)
    @DisplayName("Test if credentials are invalid on login")
    public void testLoginInvalidCredentialsException() {
        Assertions.assertTrue(userDAO._getAuths().isEmpty());
        Assertions.assertTrue(userDAO._getUsers().isEmpty());

        populateDummyUsers();
        populateDummyAuths();

        UserData dummyUser = new UserData("johnny", "WRONG-PASSWORD", "johnny@email.com");

        Assertions.assertThrows(InvalidCredentialsException.class, () -> {
            userService.login(dummyUser);
        });
    }

    @Test
    @Order(8)
    @DisplayName("Test if login creates auth")
    public void testLoginCreatesAuth() {
        Assertions.assertTrue(userDAO._getAuths().isEmpty());
        Assertions.assertTrue(userDAO._getUsers().isEmpty());

        populateDummyUsers();

        UserData dummyUser = new UserData("johnny", "1234", "johnny@email.com");

        try {
            userService.login(dummyUser);
        } catch (Exception _) {

        }

        Assertions.assertFalse(userDAO._getAuths().isEmpty());
    }

    @Test
    @Order(9)
    @DisplayName("Test if logging in returns reasonable results")
    public void testLoggingInReturnsReasonableResults() {
        Assertions.assertTrue(userDAO._getUsers().isEmpty());
        Assertions.assertTrue(userDAO._getAuths().isEmpty());

        populateDummyUsers();

        UserData dummyUser = new UserData("johnny", "1234", "johnny@email.com");

        LoginResult result = new LoginResult(null, null);

        try {
            result = userService.login(dummyUser);
        } catch (Exception _) {

        }

        Assertions.assertEquals(dummyUser.username(), result.username());
        Assertions.assertFalse(result.authToken().isBlank());
    }
}
