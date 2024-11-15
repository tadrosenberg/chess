package service;

import dataaccess.*;
import exception.ServiceException;
import model.AuthData;
import model.UserData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import result.LoginResult;

import static org.junit.jupiter.api.Assertions.*;

class UserServiceTest {

    private UserService userService;
    private SQLUserDAO userDAO;
    private SQLAuthDAO authDAO;

    // Constants
    private static final String VALID_USERNAME = "validUser";
    private static final String VALID_PASSWORD = "validPassword";
    private static final String VALID_EMAIL = "valid@example.com";
    private static final String INVALID_USERNAME = "invalidUser";
    private static final String INVALID_PASSWORD = "invalidPassword";
    private static final String INVALID_AUTH_TOKEN = "invalidToken";

    @BeforeEach
    void setUp() throws ServiceException, DataAccessException {
        // Initialize DAOs and service for each test
        userDAO = new SQLUserDAO();
        authDAO = new SQLAuthDAO();

        // Clear the database
        userDAO.clearUserData();
        authDAO.clearAuthData();

        // Initialize the UserService
        userService = new UserService(userDAO, authDAO);
    }

    // Test Register
    @Test
    void testRegister() throws DataAccessException, ServiceException {
        // Positive case: Register a new user
        UserData newUser = new UserData(VALID_USERNAME, VALID_PASSWORD, VALID_EMAIL);
        AuthData authData = userService.register(newUser);

        assertNotNull(authData);
        assertEquals(VALID_USERNAME, authData.username());
    }

    @Test
    void testRegisterMissingData() {
        // Negative case: Register with missing username
        UserData invalidUser = new UserData("", VALID_PASSWORD, VALID_EMAIL);
        UserData finalInvalidUser = invalidUser;
        assertThrows(ServiceException.class, () -> userService.register(finalInvalidUser));

        // Negative case: Register with missing password
        invalidUser = new UserData(VALID_USERNAME, "", VALID_EMAIL);
        UserData finalInvalidUser1 = invalidUser;
        assertThrows(ServiceException.class, () -> userService.register(finalInvalidUser1));

        // Negative case: Register with missing email
        invalidUser = new UserData(VALID_USERNAME, VALID_PASSWORD, "");
        UserData finalInvalidUser2 = invalidUser;
        assertThrows(ServiceException.class, () -> userService.register(finalInvalidUser2));
    }

    // Test Login
    @Test
    void testLogin() throws DataAccessException, ServiceException {
        // Positive case: Log in with correct credentials
        UserData newUser = new UserData(VALID_USERNAME, VALID_PASSWORD, VALID_EMAIL);
        userService.register(newUser);

        UserData loginUser = new UserData(VALID_USERNAME, VALID_PASSWORD, VALID_EMAIL);
        LoginResult loginResult = userService.login(loginUser);

        assertNotNull(loginResult);
        assertEquals(VALID_USERNAME, loginResult.username());
        assertNotNull(loginResult.authToken());
    }

    @Test
    void testLoginInvalidUsername() {
        // Negative case: Login with invalid username
        UserData invalidUser = new UserData(INVALID_USERNAME, VALID_PASSWORD, VALID_EMAIL);
        assertThrows(ServiceException.class, () -> userService.login(invalidUser));
    }

    @Test
    void testLoginInvalidPassword() throws DataAccessException, ServiceException {
        // Negative case: Login with incorrect password
        UserData newUser = new UserData(VALID_USERNAME, VALID_PASSWORD, VALID_EMAIL);
        userService.register(newUser);

        UserData invalidUser = new UserData(VALID_USERNAME, INVALID_PASSWORD, VALID_EMAIL);
        assertThrows(ServiceException.class, () -> userService.login(invalidUser));
    }

    // Test Logout
    @Test
    void testLogout() throws DataAccessException, ServiceException {
        // Positive case: Log out a user
        UserData newUser = new UserData(VALID_USERNAME, VALID_PASSWORD, VALID_EMAIL);
        AuthData authData = userService.register(newUser);

        assertDoesNotThrow(() -> userService.logout(authData.authToken()));

        // Negative case: Ensure token is no longer valid after logout
        assertThrows(ServiceException.class, () -> userService.logout(authData.authToken()));
    }

    @Test
    void testLogoutInvalidToken() {
        // Negative case: Logout with invalid token
        assertThrows(ServiceException.class, () -> userService.logout(INVALID_AUTH_TOKEN));
    }
}
