package shared;

import chess.ChessGame;
import dataaccess.exceptions.*;
import model.GameData;
import org.junit.jupiter.api.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

abstract public class GameServiceTests extends ServiceTests {
    private Collection<GameData> populateDummyGames() throws DataAccessException {
        ArrayList<GameData> dummyGames = new ArrayList<GameData>(List.of(
                new GameData(
                        1,
                        "johnny",
                        "megan",
                        "johnny-vs-megan",
                        new ChessGame()
                ),
                new GameData(
                        2,
                        "charlie",
                        null,
                        "charlie-vs-null",
                        new ChessGame()
                ),
                new GameData(
                        3,
                        null,
                        null,
                        "null-vs-null",
                        new ChessGame()
                )
        ));
        gameDAO.setGames(dummyGames);

        return dummyGames;
    }

    /*
     * ==== Game List Tests ====
     * */

    @Test
    @Order(1)
    @DisplayName("Test auth token not found on game list")
    public void testGameListAuthNotFound() throws DataAccessException {
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
    public void testGameListReturnsCorrectly() throws DataAccessException {
        populateDummyUsers();
        populateDummyAuths();
        Collection<GameData> dummyGames = populateDummyGames();

        String dummyAuthToken = "1234-4312-1234-0175";

        Collection<GameData> gameList = new ArrayList<>();

        try {
            gameList = gameService.listGames(dummyAuthToken);
        } catch (Exception _) {

        }

        Assertions.assertArrayEquals(dummyGames.toArray(), gameList.toArray());
    }

    @Test
    @Order(3)
    @DisplayName("Test create game throws on invalid token")
    public void testCreateGameAuthTokenNotFound() throws DataAccessException {
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
    public void testCreateGameInvalidGameName() throws DataAccessException {
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
    public void testCreateGameSavesToDAO() throws DataAccessException {
        populateDummyUsers();
        populateDummyAuths();

        String dummyAuthToken = "1234-4312-1234-0175";

        Assertions.assertEquals(0, gameDAO.getGameList().size());

        try {
            gameService.createGame(dummyAuthToken, "game123");
        } catch (Exception _) {

        }

        Assertions.assertEquals(1, gameDAO.getGameList().size());
    }

    @Test
    @Order(6)
    @DisplayName("Test create game returns valid game")
    public void testCreateGameReturnsValidGame() throws DataAccessException {
        populateDummyUsers();
        populateDummyAuths();

        String dummyAuthToken = "1234-4312-1234-0175";

        Assertions.assertEquals(0, gameDAO.getGameList().size());

        GameData dummyData = new GameData(
                1,
                null,
                null,
                "game123",
                new ChessGame()
        );

        GameData data = new GameData(
                1,
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

    @Test
    @Order(7)
    @DisplayName("Test join game auth token not found")
    public void testJoinGameAuthNotFound() throws DataAccessException {
        populateDummyUsers();
        populateDummyAuths();
        populateDummyGames();

        String dummyAuthToken = "INVALID-TOKEN";
        int dummyID = 1;
        ChessGame.TeamColor dummyTeam = ChessGame.TeamColor.WHITE;

        Assertions.assertThrows(AuthTokenNotFoundException.class, () -> {
            gameService.joinGame(dummyAuthToken, dummyID, dummyTeam);
        });
    }

    @Test
    @Order(7)
    @DisplayName("Test join game game ID not found")
    public void testJoinGameIDNotFound() throws DataAccessException {
        populateDummyUsers();
        populateDummyAuths();
        populateDummyGames();

        String dummyAuthToken = "1234-4312-1234-0175";
        int dummyID = 5;
        ChessGame.TeamColor dummyTeam = ChessGame.TeamColor.WHITE;

        Assertions.assertThrows(GameNotFoundException.class, () -> {
            gameService.joinGame(dummyAuthToken, dummyID, dummyTeam);
        });
    }

    @Test
    @Order(8)
    @DisplayName("Test join game with already taken team color")
    public void testJoinGameAlreadyTakenTeam() throws DataAccessException {
        populateDummyUsers();
        populateDummyAuths();
        populateDummyGames();

        String dummyAuthToken = "1234-4312-1234-0175";
        int dummyID = 1;
        ChessGame.TeamColor dummyTeam = ChessGame.TeamColor.WHITE;

        try {
            gameService.joinGame(dummyAuthToken, dummyID, dummyTeam);
        } catch(Exception _) {

        }

        Assertions.assertThrows(AlreadyTakenException.class, () -> {
            gameService.joinGame(dummyAuthToken, dummyID, dummyTeam);
        });
    }

    @Test
    @Order(9)
    @DisplayName("Test join game updates game")
    public void testJoinGameUpdatesGame() throws DataAccessException {
        populateDummyUsers();
        populateDummyAuths();
        populateDummyGames();

        String dummyAuthToken = "1234-4312-1234-0175";
        String dummyUsername = "johnny";

        int dummyID = 3;
        ChessGame.TeamColor dummyTeam = ChessGame.TeamColor.WHITE;

        Assertions.assertNull(gameDAO.getGame(dummyID).whiteUsername());

        try {
            gameService.joinGame(dummyAuthToken, dummyID, dummyTeam);
        } catch(Exception e) {
            throw new RuntimeException(e);
        }

        Assertions.assertEquals(dummyUsername, gameDAO.getGame(dummyID).whiteUsername());
    }
}
