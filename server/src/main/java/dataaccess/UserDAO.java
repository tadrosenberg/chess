package dataaccess;

import model.UserData;
import service.ServiceException;

public interface UserDAO {
    UserData createUser(UserData user) throws ServiceException;

    UserData getUser(String username) throws DataAccessException;

    void clearUserData() throws DataAccessException;
}
