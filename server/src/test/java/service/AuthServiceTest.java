package service;

import dataaccess.MemoryAuthDAO;
import dataaccess.DataAccessException;
import model.AuthData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AuthServiceTest {

    private AuthService authService;
    private MemoryAuthDAO authDAO;

    @BeforeEach
    void setUp() {
        // Use the in-memory DAO for testing
        authDAO = new MemoryAuthDAO();
        authService = new AuthService(authDAO);
    }

    @Test
    void testCreateAuth() throws DataAccessException {
        // Create an auth token for a user
        AuthData authData = authService.createAuth("user1");

        // Assert that the token was successfully created
        assertNotNull(authData);
        assertNotNull(authData.authToken());
        assertEquals("user1", authData.username());

        // Verify that the token can be retrieved
        AuthData retrievedAuth = authService.getAuth(authData.authToken());
        assertEquals(authData.authToken(), retrievedAuth.authToken());
        assertEquals(authData.username(), retrievedAuth.username());
    }

    @Test
    void testGetAuthInvalidToken() {
        // Try to retrieve a non-existent token, expecting an exception
        assertThrows(DataAccessException.class, () -> authService.getAuth("invalidToken"));
    }

    @Test
    void testDeleteAuth() throws DataAccessException {
        // Create an auth token
        AuthData authData = authService.createAuth("user1");

        // Assert that the token was successfully created
        assertNotNull(authData.authToken());

        // Delete the auth token
        authService.deleteAuth(authData.authToken());

        // Verify that the token no longer exists
        assertThrows(DataAccessException.class, () -> authService.getAuth(authData.authToken()));
    }
}
