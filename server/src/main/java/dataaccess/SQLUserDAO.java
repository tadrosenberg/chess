package dataaccess;

import model.AuthData;
import model.UserData;
import org.mindrot.jbcrypt.BCrypt;
import service.ServiceException;

import java.sql.ResultSet;
import java.sql.SQLException;

public class SQLUserDAO extends AbstractDAO implements UserDAO {
    private final String[] createStatements = {
            """
            CREATE TABLE IF NOT EXISTS  user (
              `username` varchar(256) NOT NULL,
              `password` varchar(256) NOT NULL,
              `email` varchar(256) NOT NULL,
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
    public UserData createUser(UserData user) throws DataAccessException, ServiceException {
        if (getUser(user.username()) != null) {
            throw new ServiceException(403, "Error: already taken");
        }
        String hashedPassword = BCrypt.hashpw(user.password(), BCrypt.gensalt());
        var statement = "INSERT INTO user (username, password, email) VALUES (?, ?, ?)";
        executeUpdate(statement, user.username(), hashedPassword, user.email());
        return new UserData(user.username(), hashedPassword, user.email());
    }

    @Override
    public UserData getUser(String username) throws DataAccessException {
        var statement = "SELECT username, password, email FROM user WHERE username=?";
        try (ResultSet rs = executeQuery(statement, username)) {  // Use try-with-resources here
            if (rs.next()) {
                return new UserData(
                        rs.getString("username"),
                        rs.getString("password"),
                        rs.getString("email")
                );
            }
        } catch (SQLException e) {
            throw new DataAccessException(e.getMessage());
        }

        return null;
    }

    @Override
    public void clearUserData() throws DataAccessException {
        var statement = "TRUNCATE user";
        executeUpdate(statement);
    }

}
