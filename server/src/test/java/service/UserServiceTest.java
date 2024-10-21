package service;

import dataaccess.MemoryUserDAO;
import dataaccess.MemoryAuthDAO;
import model.UserData;
import model.AuthData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import service.ServiceException;

import static org.junit.jupiter.api.Assertions.*;

class UserServiceTest {

    private UserService userService;
    private MemoryUserDAO userDAO;
    private MemoryAuthDAO authDAO;

    @BeforeEach
    void setUp() {
        // Initialize the in-memory DAO and the UserService for testing
        userDAO = new MemoryUserDAO();
        authDAO = new MemoryAuthDAO();  // AuthDAO is needed for user registration
        userService = new UserService(userDAO, authDAO);
    }

    @Test
    void testRegisterUser() throws Exception {
        // Create a new user
        AuthData authData = userService.register(new UserData("user1", "password1", "user1@example.com"));

        // Assert that the user was created successfully
        UserData user = userDAO.getUser("user1");
        assertNotNull(user);
        assertEquals("user1", user.username());
        assertEquals("password1", user.password());
        assertEquals("user1@example.com", user.email());

        // Assert that auth token was generated
        assertNotNull(authData.authToken());
        assertEquals("user1", authData.username());
    }

    @Test
    void testRegisterDuplicateUser() {
        // First user registration should succeed
        assertDoesNotThrow(() -> userService.register(new UserData("user1", "password1", "user1@example.com")));

        // Trying to register a user with the same username should throw a ServiceException
        ServiceException exception = assertThrows(ServiceException.class, () -> userService.register(new UserData("user1", "password2", "user2@example.com")));
        assertEquals(403, exception.getStatusCode());  // Status code should be 403 for "Username already exists"
        assertEquals("Username already exists", exception.getMessage());
    }

    @Test
    void testRegisterInvalidUsername() {
        // Registering a user with an invalid username (empty) should throw a ServiceException
        ServiceException exception = assertThrows(ServiceException.class, () -> userService.register(new UserData("", "password1", "user1@example.com")));
        assertEquals(400, exception.getStatusCode());  // Status code should be 400 for "Bad request"
        assertEquals("Error: bad request - missing required fields", exception.getMessage());
    }

    @Test
    void testRegisterInvalidPassword() {
        // Registering a user with an invalid password (empty) should throw a ServiceException
        ServiceException exception = assertThrows(ServiceException.class, () -> userService.register(new UserData("user1", "", "user1@example.com")));
        assertEquals(400, exception.getStatusCode());  // Status code should be 400 for "Bad request"
        assertEquals("Error: bad request - missing required fields", exception.getMessage());
    }

    @Test
    void testRegisterInvalidEmail() {
        // Registering a user with an invalid email (empty) should throw a ServiceException
        ServiceException exception = assertThrows(ServiceException.class, () -> userService.register(new UserData("user1", "password1", "")));
        assertEquals(400, exception.getStatusCode());  // Status code should be 400 for "Bad request"
        assertEquals("Error: bad request - missing required fields", exception.getMessage());
    }
}
