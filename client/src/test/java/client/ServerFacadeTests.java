package client;

import client.exceptions.FacadeException;
import dataaccess.exceptions.DataAccessException;
import dataaccess.sql.SQLAppDAO;
import dataaccess.sql.SQLGameDAO;
import dataaccess.sql.SQLUserDAO;
import model.AuthData;
import model.UserData;
import org.junit.jupiter.api.*;
import server.Server;
import service.AppService;
import service.GameService;
import service.UserService;


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
    public void registerBadRequestTest() throws FacadeException {
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
}
