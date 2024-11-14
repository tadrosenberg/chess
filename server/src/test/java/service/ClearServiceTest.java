package service;

import dataaccess.*;
import model.UserData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ClearServiceTest {

    private ClearService clearService;
    private MemoryUserDAO userDAO;
    private MemoryAuthDAO authDAO;
    private MemoryGameDAO gameDAO;

    @BeforeEach
    void setUp() {
        authDAO = new MemoryAuthDAO();
        userDAO = new MemoryUserDAO();
        gameDAO = new MemoryGameDAO();
        clearService = new ClearService(userDAO, authDAO, gameDAO);
    }

    @Test
    void testClearDataSuccess() throws DataAccessException, exception.ServiceException {
        // Add some dummy data
        authDAO.createAuth("AUTH_TOKEN");
        userDAO.createUser(new UserData("testUser", "password", "email@example.com"));
        gameDAO.createGame("testGame");

        // Clear all data
        clearService.clear();

        // Assert all data is cleared
        assertNull(authDAO.getAuth("AUTH_TOKEN"));
        assertNull(userDAO.getUser("testUser"));
        assertNull(gameDAO.getGame(1));
    }

    @Test
    void testClearDataFailure() throws DataAccessException {
        // Clear data without adding anything
        clearService.clear();

        // Assert DAO methods return null
        assertNull(authDAO.getAuth("AUTH_TOKEN"));
        assertNull(userDAO.getUser("testUser"));
        assertNull(gameDAO.getGame(1));
    }
}
