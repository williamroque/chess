package passoff.service;

import chess.ChessGame;
import dataaccess.exceptions.AlreadyTakenException;
import dataaccess.exceptions.AuthTokenNotFoundException;
import dataaccess.exceptions.InvalidCredentialsException;
import dataaccess.exceptions.InvalidGameNameException;
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

import java.util.ArrayList;
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
        gameService = new GameService(gameDAO, userDAO);
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

        Collection<GameData> gameList = new ArrayList<>();

        try {
            gameList = gameService.listGames(dummyAuthToken);
        } catch (Exception _) {

        }

        Assertions.assertEquals(dummyGames.values(), gameList);
    }

    @Test
    @Order(3)
    @DisplayName("Test create game throws on invalid token")
    public void testCreateGameAuthTokenNotFound() {
        populateDummyUsers();
        populateDummyAuths();
        populateDummyGames();

        String dummyAuthToken = "INVALID-TOKEN";

        Assertions.assertThrows(AuthTokenNotFoundException.class, () -> {
            gameService.createGame(dummyAuthToken, "game");
        });
    }

    @Test
    @Order(4)
    @DisplayName("Test create game throws on invalid game name")
    public void testCreateGameInvalidGameName() {
        populateDummyUsers();
        populateDummyAuths();
        populateDummyGames();

        String dummyAuthToken = "1234-4312-1234-0175";

        Assertions.assertThrows(InvalidGameNameException.class, () -> {
            gameService.createGame(dummyAuthToken, "");
        });
    }

    @Test
    @Order(5)
    @DisplayName("Test create game saves to DAO")
    public void testCreateGameSavesToDAO() {
        populateDummyUsers();
        populateDummyAuths();

        String dummyAuthToken = "1234-4312-1234-0175";

        Assertions.assertEquals(0, gameDAO._getGames().size());

        try {
            gameService.createGame(dummyAuthToken, "game123");
        } catch (Exception _) {

        }

        Assertions.assertEquals(1, gameDAO._getGames().size());
    }

    @Test
    @Order(6)
    @DisplayName("Test create game returns valid game")
    public void testCreateGameReturnsValidGame() {
        populateDummyUsers();
        populateDummyAuths();

        String dummyAuthToken = "1234-4312-1234-0175";

        Assertions.assertEquals(0, gameDAO._getGames().size());

        GameData dummyData = new GameData(
                0,
                null,
                null,
                "game123",
                new ChessGame()
        );

        GameData data = new GameData(
                0,
                null,
                null,
                null,
                null
        );

        try {
            data = gameService.createGame(dummyAuthToken, "game123");
        } catch (Exception _) {

        }

        Assertions.assertEquals(dummyData, data);
    }
}
