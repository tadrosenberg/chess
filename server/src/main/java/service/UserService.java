package service;

import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import dataaccess.UserDAO;
import model.AuthData;
import model.UserData;

public class UserService {

    private final UserDAO userDAO;
    private final AuthDAO authDAO;

    public UserService(UserDAO userDAO, AuthDAO authDAO) {
        this.userDAO = userDAO;
        this.authDAO = authDAO;
    }

    // Register a new user and return their auth token
    public AuthData register(UserData user) throws DataAccessException {
        // Check if the username is already taken
        if (userDAO.getUser(user.username()) != null) {
            throw new DataAccessException("Username already taken.");
        }

        // Store the new user in the database
        userDAO.createUser(user);

        // Generate an auth token for the new user
        return authDAO.createAuth(user.username());
    }

    // Log in an existing user and return a new auth token
    public AuthData login(UserData user) throws DataAccessException {
        // Check if the user exists in the database
        UserData existingUser = userDAO.getUser(user.username());
        if (existingUser == null) {
            throw new DataAccessException("User not found.");
        }

        // Verify the password (you might want to add password hashing here)
        if (!existingUser.password().equals(user.password())) {
            throw new DataAccessException("Incorrect password.");
        }

        // Generate a new auth token for the logged-in user
        return authDAO.createAuth(user.username());
    }

    // Log out a user by invalidating their auth token
    public void logout(AuthData auth) throws DataAccessException {
        // Invalidate the user's auth token by deleting it
        authDAO.deleteAuth(auth.authToken());
    }

    // Optional: Helper function to check if a username is available
    public boolean isUsernameAvailable(String username) throws DataAccessException {
        return userDAO.getUser(username) == null;
    }
}