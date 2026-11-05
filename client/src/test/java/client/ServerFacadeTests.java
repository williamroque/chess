package client;

import client.exceptions.FacadeException;
import dataaccess.exceptions.DataAccessException;
import dataaccess.sql.SQLAppDAO;
import dataaccess.sql.SQLGameDAO;
import dataaccess.sql.SQLUserDAO;
import model.AuthData;
import model.GameData;
import model.UserData;
import org.junit.jupiter.api.*;
import server.Server;
import service.AppService;
import service.GameService;
import service.UserService;
import service.results.LoginResult;


public class ServerFacadeTests {

    private static Server server;
    private static ServerFacade facade;

    private static SQLUserDAO userDAO;
    private static SQLGameDAO gameDAO;
    private static SQLAppDAO appDAO;

    private static UserService userService;
    private static GameService gameService;
    private static AppService appService;

    @BeforeAll
    public static void init() {
        server = new Server();
        var port = server.run(0);

        userDAO = new SQLUserDAO();
        gameDAO = new SQLGameDAO();
        appDAO = new SQLAppDAO(userDAO, gameDAO);

        userService = new UserService(userDAO);
        gameService = new GameService(gameDAO, userDAO);
        appService = new AppService(appDAO);

        facade = new ServerFacade("http://localhost:" + port);

        System.out.println("Started test HTTP server on " + port);
    }

    @AfterAll
    static void stopServer() {
        server.stop();
    }

    @BeforeEach
    void clearDatabase() throws DataAccessException {
        appService.clear();
    }

    @Test
    public void clearTest() throws DataAccessException, FacadeException {
        userService.register(new UserData(
                "johnny",
                "johnrocks123",
                "john@apple.com"
        ));
        userService.register(new UserData(
                "jenny",
                "jennythegirl",
                "jenny@gmail.com"
        ));

        Assertions.assertFalse(userDAO.getUsers().isEmpty());

        facade.clear();

        Assertions.assertTrue(userDAO.getUsers().isEmpty());
    }

    @Test
    public void registerSuccessfullyTest() throws FacadeException, DataAccessException {
        AuthData auth = facade.register(new UserData(
                "marcus",
                "marktheman",
                "mark@yahoo.com"
        ));

        Assertions.assertNotNull(userDAO.getUser("marcus"));
        Assertions.assertTrue(userDAO.authExists(auth.authToken()));
    }

    @Test
    public void registerBadRequestTest() {
        UserData dummyUser = new UserData(null, null, null);

        int thrownCode = 0;

        try {
            facade.register(dummyUser);
        } catch (FacadeException e) {
            thrownCode = e.statusCode;
            System.out.println(e.getMessage());
        }

        Assertions.assertEquals(400, thrownCode);
    }

    @Test
    public void registerAlreadyTakenTest() throws FacadeException {
        UserData dummyUser = new UserData(
                "marcus",
                "marktheman",
                "mark@yahoo.com"
        );

        facade.register(dummyUser);

        int thrownCode = 0;

        try {
            facade.register(dummyUser);
        } catch (FacadeException e) {
            thrownCode = e.statusCode;
            System.out.println(e.getMessage());
        }

        Assertions.assertEquals(403, thrownCode);
    }

    @Test
    public void loginSuccessfullyTest() throws FacadeException, DataAccessException {
        UserData dummyUser = new UserData(
                "jenny",
                "jennythegirl",
                "jenny@gmail.com"
        );

        userService.register(dummyUser);

        AuthData auth = facade.login(dummyUser);

        Assertions.assertTrue(auth != null && userDAO.authExists(auth.authToken()));
    }

    @Test
    public void loginBadRequestTest() {
        UserData dummyUser = new UserData(null, null, null);

        int thrownCode = 0;

        try {
            facade.login(dummyUser);
        } catch (FacadeException e) {
            thrownCode = e.statusCode;
            System.out.println(e.getMessage());
        }

        Assertions.assertEquals(400, thrownCode);
    }

    @Test
    public void loginInvalidCredentialsTest() throws FacadeException {
        UserData dummyUser = new UserData(
                "marcus",
                "marktheman",
                "mark@yahoo.com"
        );

        facade.register(dummyUser);

        int thrownCode = 0;

        try {
            facade.login(new UserData(
                    "marcus",
                    "johnnytheguy",
                    "mark@yahoo.com"
            ));
        } catch (FacadeException e) {
            thrownCode = e.statusCode;
            System.out.println(e.getMessage());
        }

        Assertions.assertEquals(401, thrownCode);
    }

    @Test
    public void logoutSuccessfullyTest() throws FacadeException, DataAccessException {
        UserData dummyUser = new UserData(
                "jenny",
                "jennythegirl",
                "jenny@gmail.com"
        );
        LoginResult loginResult = userService.register(dummyUser);
        String authToken = loginResult.authToken();

        Assertions.assertTrue(userDAO.authExists(authToken));

        facade.logout(new AuthData(authToken, "jenny"));

        Assertions.assertFalse(userDAO.authExists(authToken));
    }

    @Test
    public void logoutInvalidCredentialsTest() throws DataAccessException {
        UserData dummyUser = new UserData(
                "jenny",
                "jennythegirl",
                "jenny@gmail.com"
        );
        LoginResult loginResult = userService.register(dummyUser);
        String authToken = loginResult.authToken();

        Assertions.assertTrue(userDAO.authExists(authToken));

        int thrownCode = 0;

        try {
            facade.logout(new AuthData("FAKE-AUTH-TOKEN", "jenny"));
        } catch (FacadeException e) {
            thrownCode = e.statusCode;
            System.out.println(e.getMessage());
        }

        Assertions.assertEquals(401, thrownCode);
    }

    @Test
    public void listGamesSuccessfullyTest() throws FacadeException, DataAccessException {
        UserData dummyUser = new UserData(
                "jenny",
                "jennythegirl",
                "jenny@gmail.com"
        );
        LoginResult loginResult = userService.register(dummyUser);
        String authToken = loginResult.authToken();

        gameService.createGame(authToken, "game-1");
        gameService.createGame(authToken, "game-2");

        Assertions.assertEquals(
                gameService.listGames(authToken),
                facade.listGames(new AuthData(authToken, dummyUser.username()))
        );
    }

    @Test
    public void listGamesInvalidCredentialsTest() {
        int thrownCode = 0;

        try {
           facade.listGames(new AuthData("FAKE-AUTH-TOKEN", null));
        } catch (FacadeException e) {
            thrownCode = e.statusCode;
            System.out.println(e.getMessage());
        }

        Assertions.assertEquals(401, thrownCode);
    }

    @Test
    public void createGameSuccessfullyTest() throws DataAccessException, FacadeException {
        UserData dummyUser = new UserData(
                "jenny",
                "jennythegirl",
                "jenny@gmail.com"
        );
        LoginResult loginResult = userService.register(dummyUser);
        String authToken = loginResult.authToken();

        Assertions.assertTrue(gameDAO.getGameList().games().isEmpty());

        facade.createGame(
                new AuthData(authToken, dummyUser.username()),
                "game-123"
        );

        Assertions.assertFalse(gameDAO.getGameList().games().isEmpty());
    }

    @Test
    public void createGameInvalidCredentialsTest() {
        int thrownCode = 0;

        try {
            facade.createGame(new AuthData("FAKE-AUTH-TOKEN", null), "game-123");
        } catch (FacadeException e) {
            thrownCode = e.statusCode;
            System.out.println(e.getMessage());
        }

        Assertions.assertEquals(401, thrownCode);
    }

    @Test
    public void createGameBadRequestTest() throws DataAccessException {
        UserData dummyUser = new UserData(
                "jenny",
                "jennythegirl",
                "jenny@gmail.com"
        );
        LoginResult loginResult = userService.register(dummyUser);
        String authToken = loginResult.authToken();

        int thrownCode = 0;

        try {
            facade.createGame(new AuthData(authToken, dummyUser.username()), "");
        } catch (FacadeException e) {
            thrownCode = e.statusCode;
            System.out.println(e.getMessage());
        }

        Assertions.assertEquals(400, thrownCode);
    }
}
