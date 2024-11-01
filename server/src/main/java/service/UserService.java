package service;

import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import dataaccess.UserDAO;
import model.AuthData;
import model.UserData;
import org.mindrot.jbcrypt.BCrypt;
import result.LoginResult;

public class UserService {

    private final UserDAO userDAO;
    private final AuthDAO authDAO;

    public UserService(UserDAO userDAO, AuthDAO authDAO) {
        this.userDAO = userDAO;
        this.authDAO = authDAO;
    }

    // Register a new user and return their auth token
    public AuthData register(UserData user) throws ServiceException, DataAccessException {
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

        String providedPassword = user.password();
        String hashedPassword = existingUser.password();

        if (!BCrypt.checkpw(providedPassword, hashedPassword)) {
            throw new ServiceException(401, "Error: unauthorized");
        }

        AuthData existingAuthData = authDAO.createAuth(user.username());

        // Generate a new auth token for the logged-in user
        return new LoginResult(user.username(), existingAuthData.authToken());
    }

    // Log out a user by invalidating their auth token
    public void logout(String authToken) throws ServiceException, DataAccessException {
        // Invalidate the user's auth token by deleting it
        if (authDAO.getAuth(authToken) == null) {
            throw new ServiceException(401, "Error: unauthorized");
        }
        authDAO.deleteAuth(authToken);
    }
}