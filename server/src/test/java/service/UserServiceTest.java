package service;

import dataaccess.MemoryUserDAO;
import dataaccess.DataAccessException;
import model.UserData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UserServiceTest {

    private UserService userService;
    private MemoryUserDAO userDAO;

    @BeforeEach
    void setUp() {
        // Initialize the in-memory DAO and the UserService for testing
        userDAO = new MemoryUserDAO();
        userService = new UserService(userDAO);
    }

    @Test
    void testCreateUser() throws DataAccessException {
        // Create a new user
        userService.createUser("user1", "password1", "user1@example.com");

        // Assert that the user was created successfully
        UserData user = userService.getUser("user1");
        assertNotNull(user);
        assertEquals("user1", user.username());
        assertEquals("password1", user.password());
        assertEquals("user1@example.com", user.email());
    }

    @Test
    void testCreateDuplicateUser() {
        // First user creation should succeed
        assertDoesNotThrow(() -> userService.createUser("user1", "password1", "user1@example.com"));

        // Trying to create a user with the same username should throw an exception
        assertThrows(DataAccessException.class, () -> userService.createUser("user1", "password2", "user2@example.com"));
    }

    @Test
    void testCreateUserInvalidUsername() {
        // Creating a user with an invalid username (empty) should throw an exception
        assertThrows(DataAccessException.class, () -> userService.createUser("", "password1", "user1@example.com"));
    }
}
