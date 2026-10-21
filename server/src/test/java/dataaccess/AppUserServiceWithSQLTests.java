package dataaccess;

import dataaccess.exceptions.AlreadyTakenException;
import dataaccess.exceptions.AuthTokenNotFoundException;
import dataaccess.exceptions.InvalidCredentialsException;
import model.UserData;
import org.junit.jupiter.api.*;
import service.results.LoginResult;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class AppUserServiceWithSQLTests extends ServiceWithSQLTests {
    /*
    * ==== Foundational Clear Test ====
    * */

    @Test
    @Order(1)
    @DisplayName("Test clearing in app service with users.")
    public void testAppClearsUsers() {
        populateDummyUsers();
        populateDummyAuths();

        Assertions.assertFalse(userDAO.getUsers().isEmpty());
        Assertions.assertFalse(userDAO.getAuths().isEmpty());

        appService.clear();

        Assertions.assertTrue(userDAO.getUsers().isEmpty());
        Assertions.assertTrue(userDAO.getAuths().isEmpty());
    }

    /*
     * ==== User Registration Tests ====
     * */

    @Test
    @Order(2)
    @DisplayName("Test registering new user to see if actually created")
    public void testRegisteringCreatesUsers() {
        Assertions.assertTrue(userDAO.getUsers().isEmpty());

        UserData dummyUser = new UserData("johnny", "1234", "johnny@email.com");

        try {
            userService.register(dummyUser);
        } catch (Exception _) {

        }

        Assertions.assertEquals(1, userDAO.getUsers().size());
        Assertions.assertEquals(dummyUser, userDAO.getUsers().toArray()[0]);
    }

    @Test
    @Order(3)
    @DisplayName("Test registering new user to see if auth created")
    public void testRegisteringCreatesAuth() {
        Assertions.assertTrue(userDAO.getAuths().isEmpty());

        UserData dummyUser = new UserData("johnny", "1234", "johnny@email.com");

        try {
            userService.register(dummyUser);
        } catch (Exception _) {

        }

        Assertions.assertEquals(1, userDAO.getAuths().size());
    }

    @Test
    @Order(4)
    @DisplayName("Test user already exists exception during registration")
    public void testRegisteringThrowsAlreadyExists() {
        Assertions.assertTrue(userDAO.getUsers().isEmpty());

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
        Assertions.assertTrue(userDAO.getUsers().isEmpty());

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
        Assertions.assertTrue(userDAO.getUsers().isEmpty());

        UserData dummyUser = new UserData("johnny", "1234", "johnny@email.com");

        Assertions.assertThrows(InvalidCredentialsException.class, () -> {
            userService.login(dummyUser);
        });
    }

    @Test
    @Order(7)
    @DisplayName("Test if credentials are invalid on login")
    public void testLoginInvalidCredentialsException() {
        Assertions.assertTrue(userDAO.getAuths().isEmpty());
        Assertions.assertTrue(userDAO.getUsers().isEmpty());

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
        Assertions.assertTrue(userDAO.getAuths().isEmpty());
        Assertions.assertTrue(userDAO.getUsers().isEmpty());

        populateDummyUsers();

        UserData dummyUser = new UserData("johnny", "1234", "johnny@email.com");

        try {
            userService.login(dummyUser);
        } catch (Exception _) {

        }

        Assertions.assertFalse(userDAO.getAuths().isEmpty());
    }

    @Test
    @Order(9)
    @DisplayName("Test if logging in returns reasonable results")
    public void testLoggingInReturnsReasonableResults() {
        Assertions.assertTrue(userDAO.getUsers().isEmpty());
        Assertions.assertTrue(userDAO.getAuths().isEmpty());

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

    /*
     * ==== User Logout Tests ====
     * */

    @Test
    @Order(10)
    @DisplayName("Test if missing auth token throws exception")
    public void testLogoutMissingAuthTokenException() {
        Assertions.assertTrue(userDAO.getUsers().isEmpty());
        Assertions.assertTrue(userDAO.getAuths().isEmpty());

        populateDummyUsers();
        populateDummyAuths();

        String dummyAuthToken = "INVALID-TOKEN";

        Assertions.assertThrows(AuthTokenNotFoundException.class, () -> {
            userService.logout(dummyAuthToken);
        });
    }

    @Test
    @Order(10)
    @DisplayName("Test if logout deletes auth token")
    public void testLogoutDeletesAuthToken() {
        Assertions.assertTrue(userDAO.getUsers().isEmpty());
        Assertions.assertTrue(userDAO.getAuths().isEmpty());

        populateDummyUsers();
        populateDummyAuths();

        Assertions.assertEquals(3, userDAO.getAuths().size());

        String dummyAuthToken = "1234-4312-1234-0175";

        try {
            userService.logout(dummyAuthToken);
        } catch (Exception _) {

        }

        Assertions.assertEquals(2, userDAO.getAuths().size());
    }
}
