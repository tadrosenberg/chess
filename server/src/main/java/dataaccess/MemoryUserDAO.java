package dataaccess;

import model.UserData;
import service.ServiceException;

import java.util.HashMap;
import java.util.Map;


public class MemoryUserDAO implements UserDAO {
    private final Map<String, UserData> users = new HashMap<>();

    @Override
    public UserData createUser(UserData user) throws ServiceException {
        // Check if the username already exists
        if (users.containsKey(user.username())) {
            throw new ServiceException(403, "Error: already taken");
        }

        // Add the user to the map
        users.put(user.username(), user);

        return user;
    }

    @Override
    public UserData getUser(String username) {
        return users.get(username);
    }

    @Override
    public void clearUserData() throws DataAccessException {
        users.clear();
    }
}
