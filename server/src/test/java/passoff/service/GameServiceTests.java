package passoff.service;

import chess.ChessGame;
import dataaccess.exceptions.AlreadyTakenException;
import dataaccess.exceptions.AuthTokenNotFoundException;
import dataaccess.exceptions.InvalidCredentialsException;
import dataaccess.memory.MemoryAppDAO;
import dataaccess.memory.MemoryGameDAO;
import dataaccess.memory.MemoryUserDAO;
import model.AuthData;
import model.GameData;
import model.UserData;
import org.junit.jupiter.api.*;
import service.AppService;
import service.GameService;
import service.UserService;
import service.results.LoginResult;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class GameServiceTests {
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

    private HashMap<Integer, GameData> populateDummyGames() {
        HashMap<Integer, GameData> dummyGames = new HashMap<>(Map.of(
                0, new GameData(
                        0,
                        "johnny",
                        "megan",
                        "johnny-vs-megan",
                        new ChessGame()
                ),
                1, new GameData(
                        1,
                        "charlie",
                        null,
                        "charlie-vs-null",
                        new ChessGame()
                ),
                2, new GameData(
                        2,
                        null,
                        null,
                        "null-vs-null",
                        new ChessGame()
                )
        ));
        gameDAO._setGames(dummyGames);

        return dummyGames;
    }

    /*
     * ==== Game List Tests ====
     * */

    @Test
    @Order(1)
    @DisplayName("Test auth token not found on game list")
    public void testGameListAuthNotFound() {
        populateDummyUsers();
        populateDummyAuths();
        populateDummyGames();

        String dummyAuthToken = "INVALID-TOKEN";

        Assertions.assertThrows(AuthTokenNotFoundException.class, () -> {
            gameService.listGames(dummyAuthToken);
        });
    }

    @Test
    @Order(2)
    @DisplayName("Test game list returns correctly")
    public void testGameListReturnsCorrectly() {
        populateDummyUsers();
        populateDummyAuths();
        HashMap<Integer, GameData> dummyGames = populateDummyGames();

        String dummyAuthToken = "1234-4312-1234-0175";

        Collection<GameData> gameList = gameService.listGames(dummyAuthToken);

        Assertions.assertEquals(dummyGames.values(), gameList);
    }
}
