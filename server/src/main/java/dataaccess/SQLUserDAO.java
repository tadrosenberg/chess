package dataaccess;

import model.UserData;
import service.ServiceException;

public class SQLUserDAO implements UserDAO {
    @Override
    public UserData createUser(UserData user) throws ServiceException {
        return null;
    }

    @Override
    public UserData getUser(String username) throws DataAccessException {
        return null;
    }

    @Override
    public void clearUserData() throws DataAccessException {

    }
}
