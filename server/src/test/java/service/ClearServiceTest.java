package service;

import dataaccess.MemoryAuthDAO;
import dataaccess.MemoryUserDAO;
import dataaccess.MemoryGameDAO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import service.ClearService;
import model.UserData;
import model.AuthData;
import model.GameData;

import static org.junit.jupiter.api.Assertions.*;

class ClearServiceTest {

    static final MemoryAuthDAO authDAO = new MemoryAuthDAO();
    static final MemoryUserDAO userDAO = new MemoryUserDAO();
    static final MemoryGameDAO gameDAO = new MemoryGameDAO();
    static final ClearService clearService = new ClearService(userDAO, authDAO, gameDAO);

    @BeforeEach
    void setUp() throws Exception {
        clearService.clear();
    }

    @Test
    void clearAllData() throws Exception {
        // Pre-populate DAOs with dummy data
        userDAO.createUser(new UserData("user1", "password", "email@example.com"));
        authDAO.createAuth(new AuthData("token1", "user1"));
        gameDAO.createGame("Test Game");

        // Assert that data exists before clearing
        assertNotNull(userDAO.getUser("user1"));
        assertNotNull(authDAO.getAuth("token1"));
        assertEquals(1, gameDAO.listGames().length);

        // Clear all data
        clearService.clear();

        // Assert that all data has been cleared
        assertThrows(Exception.class, () -> userDAO.getUser("user1"));
        assertThrows(Exception.class, () -> authDAO.getAuth("token1"));
        assertEquals(0, gameDAO.listGames().length);
    }

    @Test
    void clearEmptyData() throws Exception {
        // Clear when there is no data present
        clearService.clear();

        // Ensure all data remains empty without throwing exceptions
        assertThrows(Exception.class, () -> userDAO.getUser("user1"));
        assertThrows(Exception.class, () -> authDAO.getAuth("token1"));
        assertEquals(0, gameDAO.listGames().length);
    }
}
