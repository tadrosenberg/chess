package service;

import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import dataaccess.UserDAO;
import model.AuthData;
import model.UserData;
import result.LoginResult;

public class UserService {

    private final UserDAO USER_DAO;
    private final AuthDAO AUTH_DAO;

    public UserService(UserDAO userDAO, AuthDAO authDAO) {
        this.USER_DAO = userDAO;
        this.AUTH_DAO = authDAO;
    }

    // Register a new user and return their auth token
    public AuthData register(UserData user) throws ServiceException {
        if (user.username() == null || user.username().isEmpty() ||
                user.password() == null || user.password().isEmpty() ||
                user.email() == null || user.email().isEmpty()) {
            throw new ServiceException(400, "Error: Bad request");
        }
        USER_DAO.createUser(user);

        // Generate an auth token for the new user
        return AUTH_DAO.createAuth(user.username());
    }

    // Log in an existing user and return a new auth token
    public LoginResult login(UserData user) throws ServiceException, DataAccessException {
        // Check if the user exists in the database
        UserData existingUser = USER_DAO.getUser(user.username());
        if (existingUser == null) {
            throw new ServiceException(401, "Error: unauthorized");
        }

        // Verify the password (you might want to add password hashing here)
        if (!existingUser.password().equals(user.password())) {
            throw new ServiceException(401, "Error: unauthorized");
        }

        AuthData existingAuthData = AUTH_DAO.createAuth(user.username());

        // Generate a new auth token for the logged-in user
        return new LoginResult(user.username(), existingAuthData.authToken());
    }

    // Log out a user by invalidating their auth token
    public void logout(String authToken) throws ServiceException, DataAccessException {
        // Invalidate the user's auth token by deleting it
        if (AUTH_DAO.getAuth(authToken) == null) {
            throw new ServiceException(401, "Error: unauthorized");
        }
        AUTH_DAO.deleteAuth(authToken);
    }
}