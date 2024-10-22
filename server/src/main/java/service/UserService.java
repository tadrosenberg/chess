package service;

import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import dataaccess.UserDAO;
import model.AuthData;
import model.UserData;
import request.LogoutRequest;
import result.LoginResult;

public class UserService {

    private final UserDAO userDAO;
    private final AuthDAO authDAO;

    public UserService(UserDAO userDAO, AuthDAO authDAO) {
        this.userDAO = userDAO;
        this.authDAO = authDAO;
    }

    // Register a new user and return their auth token
    public AuthData register(UserData user) throws ServiceException {
        if (user.username() == null || user.username().isEmpty() ||
                user.password() == null || user.password().isEmpty() ||
                user.email() == null || user.email().isEmpty()) {
            throw new ServiceException(400, "Error: Bad request");
        }
        userDAO.createUser(user);

        // Generate an auth token for the new user
        return authDAO.createAuth(user.username());
    }

    // Log in an existing user and return a new auth token
    public LoginResult login(UserData user) throws ServiceException, DataAccessException {
        // Check if the user exists in the database
        UserData existingUser = userDAO.getUser(user.username());
        if (existingUser == null) {
            throw new ServiceException(401, "Error: unauthorized");
        }

        // Verify the password (you might want to add password hashing here)
        if (!existingUser.password().equals(user.password())) {
            throw new ServiceException(401, "Error: unauthorized");
        }

        AuthData existingAuthData = authDAO.createAuth(user.username());

        // Generate a new auth token for the logged-in user
        return new LoginResult(user.username(), existingAuthData.authToken());
    }

    // Log out a user by invalidating their auth token
    public void logout(LogoutRequest auth) throws ServiceException, DataAccessException {
        // Invalidate the user's auth token by deleting it
        if (authDAO.getAuth(auth.authToken()) == null) {
            throw new ServiceException(401, "Error: unauthorized");
        }
        authDAO.deleteAuth(auth.authToken());
    }

    // Optional: Helper function to check if a username is available
    public boolean isUsernameAvailable(String username) throws DataAccessException {
        return userDAO.getUser(username) == null;
    }
}