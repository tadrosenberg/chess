package service;

import dataaccess.DataAccessException;
import dataaccess.UserDAO;
import model.UserData;

public class UserService {
    private final UserDAO userDAO;

    public UserService(UserDAO userDAO) {
        this.userDAO = userDAO;
    }

    public void createUser(String username, String password, String email) throws DataAccessException {
        if (username == null || username.isEmpty()) {
            throw new DataAccessException("Username cannot be empty.");
        }

        UserData newUser = new UserData(username, password, email);

        userDAO.createUser(newUser);
    }

    public UserData getUser(String username) throws DataAccessException {
        return userDAO.getUser(username);
    }
}
