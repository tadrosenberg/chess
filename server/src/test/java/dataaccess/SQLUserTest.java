package dataaccess;

import model.UserData;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mindrot.jbcrypt.BCrypt;
import service.ServiceException;

import static org.junit.jupiter.api.Assertions.*;

class SQLUserTest {

    private SQLUserDAO userDAO;

    @BeforeEach
    void setUp() throws DataAccessException, ServiceException {
        userDAO = new SQLUserDAO();  // Initialize the DAO and configure the database
    }

    @AfterEach
    void tearDown() throws DataAccessException {
        userDAO.clearUserData();  // Clear user data after each test
    }

    @Test
    void createUserValidDataShouldCreateUser() throws DataAccessException, ServiceException {
        // Positive test: create a valid user and check if it was inserted correctly
        UserData user = new UserData("testUser", "testPassword", "testUser@example.com");
        UserData createdUser = userDAO.createUser(user);

        assertNotNull(createdUser, "UserData should not be null");
        assertEquals(user.username(), createdUser.username(), "Username should match the input");
        assertEquals(user.email(), createdUser.email(), "Email should match the input");

        // Verify password hashing (the stored password should not be the same as the plain password)
        assertNotEquals(user.password(), createdUser.password(), "Password should be hashed and not match plaintext");

        // Verify the hashed password is stored in the database and can be verified
        UserData retrievedUser = userDAO.getUser(user.username());
        assertNotNull(retrievedUser, "Retrieved user should not be null");
        assertTrue(BCrypt.checkpw("testPassword", retrievedUser.password()), "Stored password should match hashed version of the original password");
    }

    @Test
    void createUserDuplicateUsernameShouldThrowException() throws DataAccessException, ServiceException {
        // Negative test: create a user, then try creating another user with the same username
        UserData user = new UserData("testUser", "testPassword", "testUser@example.com");
        userDAO.createUser(user);

        UserData duplicateUser = new UserData("testUser", "anotherPassword", "duplicate@example.com");
        assertThrows(ServiceException.class, () -> userDAO.createUser(duplicateUser),
                "Creating a user with a duplicate username should throw a DataAccessException");
    }

    @Test
    void getUserNonExistentUserShouldReturnNull() throws DataAccessException {
        // Negative test: try to get a user that doesn't exist
        UserData result = userDAO.getUser("nonExistentUser");
        assertNull(result, "Expected getUser to return null for a non-existent user");
    }

    @Test
    void clearUserDataShouldRemoveAllUsers() throws DataAccessException, ServiceException {
        // Positive test: create multiple users, then clear them all
        userDAO.createUser(new UserData("user1", "password1", "user1@example.com"));
        userDAO.createUser(new UserData("user2", "password2", "user2@example.com"));

        userDAO.clearUserData();

        // Verify that no users exist after clearing data
        assertNull(userDAO.getUser("user1"), "Expected getUser to return null after clearing user data");
        assertNull(userDAO.getUser("user2"), "Expected getUser to return null after clearing user data");
    }
}
