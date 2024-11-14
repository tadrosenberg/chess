package dataaccess;

import model.UserData;
import exception.ServiceException;

public interface UserDAO {
    UserData createUser(UserData user) throws ServiceException, DataAccessException;

    UserData getUser(String username) throws DataAccessException;

    void clearUserData() throws DataAccessException;
}
