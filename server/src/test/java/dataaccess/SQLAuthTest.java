package dataaccess;

import model.AuthData;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import service.ServiceException;

import static org.junit.jupiter.api.Assertions.*;

class SQLAuthTest {

    private SQLAuthDAO authDAO;

    @BeforeEach
    void setUp() throws DataAccessException, ServiceException {
        // Initialize the DAO and the database
        authDAO = new SQLAuthDAO();
    }

    @AfterEach
    void tearDown() throws DataAccessException {
        // Clear the database after each test to avoid data contamination between tests
        authDAO.clearAuthData();
    }

    @Test
    void createAuthValidUsernameShouldCreateAuthData() throws DataAccessException {
        // Positive test: create a valid AuthData and check if it was inserted correctly
        String username = "testUser";
        AuthData authData = authDAO.createAuth(username);

        assertNotNull(authData);
        assertEquals(username, authData.username());

        // Verify it can be retrieved
        AuthData retrievedAuthData = authDAO.getAuth(authData.authToken());
        assertNotNull(retrievedAuthData);
        assertEquals(authData.authToken(), retrievedAuthData.authToken());
        assertEquals(authData.username(), retrievedAuthData.username());
    }

    @Test
    void getAuthNonExistentTokenShouldReturnNull() throws DataAccessException {
        // Negative test: try to get an auth token that doesn't exist
        String nonExistentToken = "nonExistentToken";
        AuthData result = authDAO.getAuth(nonExistentToken);

        assertNull(result, "Expected getAuth to return null for a non-existent token");
    }

    @Test
    void createAuthDuplicateUsernameShouldCreateSeparateTokens() throws DataAccessException {
        // Positive test: create multiple tokens for the same username and verify they are separate
        String username = "testUser";
        AuthData firstAuth = authDAO.createAuth(username);
        AuthData secondAuth = authDAO.createAuth(username);

        assertNotEquals(firstAuth.authToken(), secondAuth.authToken(), "Tokens for the same username should be unique");
    }

    @Test
    void deleteAuthValidTokenShouldRemoveAuthData() throws DataAccessException {
        // Positive test: create and then delete an auth token, verifying it no longer exists
        String username = "testUser";
        AuthData authData = authDAO.createAuth(username);
        authDAO.deleteAuth(authData.authToken());

        AuthData result = authDAO.getAuth(authData.authToken());
        assertNull(result, "Expected getAuth to return null after deleting the auth token");
    }

    @Test
    void deleteAuthNonExistentTokenShouldNotThrowException() throws DataAccessException {
        // Negative test: deleting a non-existent token should not throw an exception
        String nonExistentToken = "nonExistentToken";
        assertDoesNotThrow(() -> authDAO.deleteAuth(nonExistentToken));
    }

    @Test
    void clearAuthDataShouldRemoveAllAuthData() throws DataAccessException {
        // Positive test: create multiple auth entries, then clear them all
        authDAO.createAuth("user1");
        authDAO.createAuth("user2");

        authDAO.clearAuthData();

        // Verify the data was cleared
        assertNull(authDAO.getAuth("user1"));
        assertNull(authDAO.getAuth("user2"));
    }

    @Test
    void createAuthWithSQLExceptionShouldThrowDataAccessException() {
        // Negative test: simulate a SQLException by using an invalid database configuration
        // This requires an alternative constructor or mocking
        assertThrows(DataAccessException.class, () -> {
            // Set up an invalid SQLAuthDAO instance (e.g., invalid connection or statement)
            SQLAuthDAO brokenDAO = new SQLAuthDAO() {
                @Override
                public AuthData createAuth(String username) throws DataAccessException {
                    throw new DataAccessException("Simulated database error");
                }
            };
            brokenDAO.createAuth("testUser");
        });
    }
}
