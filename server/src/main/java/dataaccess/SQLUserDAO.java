package dataaccess;

import model.UserData;
import service.ServiceException;

public class SQLUserDAO extends AbstractDAO implements UserDAO {
    private final String[] createStatements = {
            """
            CREATE TABLE IF NOT EXISTS  auth (
              `username` varchar(256) NOT NULL,
              `password` varchar(256) NOT NULL,
              'email' varchar(256) NOT NULL,
              PRIMARY KEY (`username`)
            ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci
            """
    };

    public SQLUserDAO() throws ServiceException, DataAccessException {
        configureDatabase();
    }

    @Override
    public String[] getCreateStatements() {
        return createStatements;
    }

    @Override
    public UserData createUser(UserData user) throws ServiceException {
        var statement = "INSERT INTO auth (token, username) VALUES (?, ?)";

        
    }

    @Override
    public UserData getUser(String username) throws DataAccessException {
        return null;
    }

    @Override
    public void clearUserData() throws DataAccessException {

    }

}
