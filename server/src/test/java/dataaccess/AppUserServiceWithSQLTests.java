package dataaccess;

import dataaccess.sql.SQLAppDAO;
import dataaccess.sql.SQLGameDAO;
import dataaccess.sql.SQLUserDAO;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.TestMethodOrder;
import service.AppService;
import service.GameService;
import service.UserService;
import shared.AppUserServiceTests;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class AppUserServiceWithSQLTests extends AppUserServiceTests {
    @BeforeAll
    public static void init() {
        try {
            DatabaseManager.configureDatabase();
        } catch (Exception e) {
            System.err.println(e);
        }

        userDAO = new SQLUserDAO();
        gameDAO = new SQLGameDAO();
        appDAO = new SQLAppDAO((SQLUserDAO) userDAO, (SQLGameDAO) gameDAO);

        userService = new UserService(userDAO);
        gameService = new GameService(gameDAO, userDAO);
        appService = new AppService(appDAO);
    }
}
