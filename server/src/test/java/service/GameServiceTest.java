package service;

import dataaccess.*;
import model.GameData;
import model.AuthData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class GameServiceTest {

    private GameService gameService;
    private MemoryAuthDAO authDAO;
    private MemoryGameDAO gameDAO;

    @BeforeEach
    void setUp() {
        authDAO = new MemoryAuthDAO();
        gameDAO = new MemoryGameDAO();
        gameService = new GameService(authDAO, gameDAO);
    }

    @Test
    void testCreateGameSuccess() throws ServiceException, DataAccessException {
        // Add an auth token
        AuthData auth = authDAO.createAuth("validUser");

        // Create a new game
        var result = gameService.createGame("testGame", auth.authToken());

        // Assert the game was created
        assertEquals(1, result.gameID());
    }

    @Test
    void testCreateGameUnauthorized() {
        // Try to create a game with an invalid auth token
        assertThrows(ServiceException.class, () -> gameService.createGame("testGame", "INVALID_AUTH_TOKEN"));
    }

    @Test
    void testJoinGameSuccess() throws ServiceException, DataAccessException {
        // Add a game and an auth token
        AuthData auth = authDAO.createAuth("validUser");
        var game = gameDAO.createGame("testGame");

        // Join the game
        gameService.joinGame(game.gameID(), "WHITE", auth.authToken());

        // Assert the white player joined
        assertEquals("validUser", gameDAO.getGame(game.gameID()).whiteUsername());
    }

    @Test
    void testJoinGameAlreadyTaken() throws ServiceException, DataAccessException {
        // Add a game and two auth tokens
        AuthData auth1 = authDAO.createAuth("validUser1");
        AuthData auth2 = authDAO.createAuth("validUser2");
        var game = gameDAO.createGame("testGame");

        // First user joins as WHITE
        gameService.joinGame(game.gameID(), "WHITE", auth1.authToken());

        // Second user tries to join as WHITE, should throw an exception
        assertThrows(ServiceException.class, () -> gameService.joinGame(game.gameID(), "WHITE", auth2.authToken()));
    }
}
