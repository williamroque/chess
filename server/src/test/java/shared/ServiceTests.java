package shared;

import dataaccess.AppDAO;
import dataaccess.GameDAO;
import dataaccess.UserDAO;
import dataaccess.exceptions.DataAccessException;
import dataaccess.sql.SQLAppDAO;
import dataaccess.sql.SQLGameDAO;
import dataaccess.sql.SQLUserDAO;
import model.AuthData;
import model.UserData;
import org.junit.jupiter.api.BeforeEach;
import org.mindrot.jbcrypt.BCrypt;
import service.AppService;
import service.GameService;
import service.UserService;

abstract public class ServiceTests {
    protected static UserDAO userDAO;
    protected static GameDAO gameDAO;
    protected static AppDAO appDAO;

    protected static UserService userService;
    protected static GameService gameService;
    protected static AppService appService;

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

    protected void populateDummyAuths() throws DataAccessException {
        userDAO.createAuth(new AuthData("1234-4312-1234-0175", "johnny"));
        userDAO.createAuth(new AuthData("0982-3917-5916-5629", "megan"));
        userDAO.createAuth(new AuthData("5017-0572-9572-4827", "charlie"));
    }
}
