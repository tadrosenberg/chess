package dataaccess;

import com.google.gson.Gson;
import model.GameData;
import chess.ChessGame;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import service.ServiceException;

import static org.junit.jupiter.api.Assertions.*;

class SQLGameTest {
    private SQLGameDAO gameDAO;
    private Gson gson;

    @BeforeEach
    void setUp() throws DataAccessException, ServiceException {
        gameDAO = new SQLGameDAO(); // Initialize the DAO and configure the database
        gson = new Gson(); // Initialize Gson for serialization/deserialization
    }

    @AfterEach
    void tearDown() throws DataAccessException {
        gameDAO.clearGameData(); // Clear game data after each test
    }

    @Test
    void createGameValidDataShouldCreateGame() throws DataAccessException {
        // Positive test: create a game and verify it was inserted correctly
        GameData game = gameDAO.createGame("testGame");

        assertNotNull(game, "GameData should not be null");
        assertEquals("testGame", game.gameName(), "Game name should match the input");
        assertNotNull(game.gameID(), "Game ID should be generated and not null");
    }

    @Test
    void getGameValidIDShouldReturnGameData() throws DataAccessException {
        // Positive test: create a game, retrieve it, and verify its data
        GameData createdGame = gameDAO.createGame("testGame");

        GameData retrievedGame = gameDAO.getGame(createdGame.gameID());
        assertNotNull(retrievedGame, "Retrieved game should not be null");
        assertEquals(createdGame.gameID(), retrievedGame.gameID(), "Game ID should match");
        assertEquals(createdGame.gameName(), retrievedGame.gameName(), "Game name should match");
    }

    @Test
    void getGameInvalidIDShouldReturnNull() throws DataAccessException {
        // Negative test: try to get a game with an invalid ID
        GameData result = gameDAO.getGame(-1);
        assertNull(result, "Expected getGame to return null for a non-existent game ID");
    }

    @Test
    void updateGameShouldModifyGameData() throws DataAccessException {
        GameData createdGame = gameDAO.createGame("testGame");
        ChessGame updatedChessGame = new ChessGame(); // Modify the ChessGame state as needed
        createdGame = new GameData(createdGame.gameID(), "player1", "player2", "updatedGame", updatedChessGame);

        gameDAO.updateGame(createdGame);

        GameData updatedGame = gameDAO.getGame(createdGame.gameID());
        assertNotNull(updatedGame, "Updated game should not be null");
        assertEquals("player1", updatedGame.whiteUsername(), "White username should match");
        assertEquals("player2", updatedGame.blackUsername(), "Black username should match");
        assertEquals("updatedGame", updatedGame.gameName(), "Game name should match the update");

        ChessGame retrievedChessGame = updatedGame.game();
        assertEquals(gson.toJson(updatedChessGame), gson.toJson(retrievedChessGame), "Game state should match");
    }

    @Test
    void listGamesShouldReturnAllGames() throws DataAccessException {
        // Positive test: create multiple games and verify they are listed
        gameDAO.createGame("testGame1");
        gameDAO.createGame("testGame2");

        GameData[] games = gameDAO.listGames();
        assertEquals(2, games.length, "List of games should contain 2 games");

        assertEquals("testGame1", games[0].gameName(), "First game name should match");
        assertEquals("testGame2", games[1].gameName(), "Second game name should match");
    }

    @Test
    void listGamesWithNoGamesShouldReturnEmptyArray() throws DataAccessException {
        // Negative test: list games when no games exist
        GameData[] games = gameDAO.listGames();
        assertEquals(0, games.length, "Expected empty array when no games exist");
    }

    @Test
    void clearGameDataShouldRemoveAllGames() throws DataAccessException {
        // Positive test: create multiple games, clear data, and verify no games exist
        gameDAO.createGame("testGame1");
        gameDAO.createGame("testGame2");

        gameDAO.clearGameData();

        GameData[] games = gameDAO.listGames();
        assertEquals(0, games.length, "Expected no games after clearing data");
    }

    @Test
    void updateGameNonExistentGameShouldNotModifyTable() throws DataAccessException {
        // Negative test: try to update a game that doesn’t exist
        ChessGame chessGame = new ChessGame(); // Example game state
        GameData nonExistentGame = new GameData(-1, "player1", "player2", "nonExistentGame", chessGame);

        // Check the number of games before the update attempt
        int initialGameCount = gameDAO.listGames().length;

        // Attempt to update the non-existent game
        gameDAO.updateGame(nonExistentGame);

        // Check the number of games after the update attempt
        int finalGameCount = gameDAO.listGames().length;

        // Verify that the number of games hasn't changed
        assertEquals(initialGameCount, finalGameCount, "Updating a non-existent game should not modify the game table");

        // Optionally, you can also ensure that a game with ID -1 (or any other test ID) does not exist
        assertNull(gameDAO.getGame(-1), "No game should exist with ID -1 after update attempt");
    }
}

