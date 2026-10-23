package service;

import dataaccess.exceptions.DataAccessException;
import dataaccess.memory.MemoryAppDAO;
import dataaccess.memory.MemoryGameDAO;
import dataaccess.memory.MemoryUserDAO;
import model.AuthData;
import model.UserData;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.mindrot.jbcrypt.BCrypt;

public class ServiceTests {
    protected static MemoryUserDAO userDAO;
    protected static MemoryGameDAO gameDAO;
    protected static MemoryAppDAO appDAO;

    protected static UserService userService;
    protected static GameService gameService;
    protected static AppService appService;


    @BeforeAll
    public static void init() {
        userDAO = new MemoryUserDAO();
        gameDAO = new MemoryGameDAO();
        appDAO = new MemoryAppDAO(userDAO, gameDAO);

        userService = new UserService(userDAO);
        gameService = new GameService(gameDAO, userDAO);
        appService = new AppService(appDAO);
    }

    @BeforeEach
    public void setup() throws DataAccessException {
        appService.clear();
    }

    protected void populateDummyUsers() {
        try {
            userDAO.createUser(new UserData("johnny", BCrypt.hashpw("1234", BCrypt.gensalt()), "johnny@email.com"));
            userDAO.createUser(new UserData("megan", BCrypt.hashpw("4321", BCrypt.gensalt()), "megan@email.com"));
            userDAO.createUser(new UserData("charlie", BCrypt.hashpw("4444", BCrypt.gensalt()), "charlie@email.com"));
        } catch (Exception _) {

        }
    }

    protected void populateDummyAuths() {
        userDAO.createAuth(new AuthData("1234-4312-1234-0175", "johnny"));
        userDAO.createAuth(new AuthData("0982-3917-5916-5629", "megan"));
        userDAO.createAuth(new AuthData("5017-0572-9572-4827", "charlie"));
    }
}
