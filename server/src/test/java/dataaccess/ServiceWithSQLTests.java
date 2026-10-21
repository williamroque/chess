package dataaccess;

import dataaccess.sql.SQLAppDAO;
import dataaccess.sql.SQLGameDAO;
import dataaccess.sql.SQLUserDAO;
import model.AuthData;
import model.UserData;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import service.AppService;
import service.GameService;
import service.UserService;

public class ServiceWithSQLTests {
    protected static SQLUserDAO userDAO;
    protected static SQLGameDAO gameDAO;
    protected static SQLAppDAO appDAO;

    protected static UserService userService;
    protected static GameService gameService;
    protected static AppService appService;


    @BeforeAll
    public static void init() {
        try {
            DatabaseManager.configureDatabase();
        } catch (Exception e) {
            System.err.println(e);
        }

        userDAO = new SQLUserDAO();
        gameDAO = new SQLGameDAO();
        appDAO = new SQLAppDAO(userDAO, gameDAO);

        userService = new UserService(userDAO);
        gameService = new GameService(gameDAO, userDAO);
        appService = new AppService(appDAO);
    }

    @BeforeEach
    public void setup() {
        appService.clear();
    }

    protected void populateDummyUsers() {
        try {
            userDAO.createUser(new UserData("johnny", "1234", "johnny@email.com"));
            userDAO.createUser(new UserData("megan", "4321", "megan@email.com"));
            userDAO.createUser(new UserData("charlie", "4444", "charlie@email.com"));
        } catch (Exception _) {

        }
    }

    protected void populateDummyAuths() {
        userDAO.createAuth(new AuthData("1234-4312-1234-0175", "johnny"));
        userDAO.createAuth(new AuthData("0982-3917-5916-5629", "megan"));
        userDAO.createAuth(new AuthData("5017-0572-9572-4827", "charlie"));
    }
}
