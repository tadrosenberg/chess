package service;

import dataaccess.AuthDAO;
import dataaccess.MemoryAuthDAO;
import dataaccess.DataAccessException;
import model.AuthData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AuthServiceTest {

    private AuthService authService;
    private AuthDAO authDAO;

    @BeforeEach
    void setUp() {
        // Initialize with memory-based DAO
        authDAO = new MemoryAuthDAO();
        authService = new AuthService(authDAO);
    }

    @Test
    void createAuthValidUsernameSuccess() throws DataAccessException {
        String username = "testUser";
        AuthData authData = authService.createAuth(username);

        assertNotNull(authData);
        assertEquals(username, authData.username());
        assertNotNull(authData.authToken());
    }

    @Test
    void createAuthNullUsernameSuccess() throws DataAccessException {
        // You may want to throw an exception or handle null username validation,
        // but for now it doesn't throw an exception, and just creates auth with null.
        AuthData authData = authService.createAuth(null);

        assertNotNull(authData);
        assertNull(authData.username());
        assertNotNull(authData.authToken());
    }

    @Test
    void getAuthValidAuthTokenSuccess() throws DataAccessException {
        String username = "testUser";
        AuthData createdAuthData = authService.createAuth(username);

        AuthData fetchedAuthData = authService.getAuth(createdAuthData.authToken());

        assertNotNull(fetchedAuthData);
        assertEquals(createdAuthData.authToken(), fetchedAuthData.authToken());
        assertEquals(username, fetchedAuthData.username());
    }

    @Test
    void getAuthInvalidAuthTokenReturnsNull() throws DataAccessException {
        AuthData authData = authService.getAuth("invalidToken");

        assertNull(authData);  // Since no exception is thrown, it should return null for invalid tokens
    }

    @Test
    void deleteAuthValidAuthTokenSuccess() throws DataAccessException {
        String username = "testUser";
        AuthData createdAuthData = authService.createAuth(username);

        // Ensure the auth token exists before deletion
        assertNotNull(authService.getAuth(createdAuthData.authToken()));

        // Delete the auth token
        authService.deleteAuth(createdAuthData.authToken());

        // After deletion, fetching the auth token should return null
        AuthData fetchedAuthData = authService.getAuth(createdAuthData.authToken());
        assertNull(fetchedAuthData);
    }

    @Test
    void deleteAuthInvalidAuthTokenThrowsException() {
        assertThrows(DataAccessException.class, () -> authService.deleteAuth("invalidToken"));
    }
}
