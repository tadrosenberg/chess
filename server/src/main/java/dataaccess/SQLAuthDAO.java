package dataaccess;

import model.AuthData;
import service.ServiceException;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class SQLAuthDAO extends AbstractDAO implements AuthDAO {

    private final String[] createStatements = {
            """
            CREATE TABLE IF NOT EXISTS  auth (
              `token` varchar(256) NOT NULL,
              `username` varchar(256) NOT NULL,
              PRIMARY KEY (`token`),
              INDEX(username)
            ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci
            """
    };

    public SQLAuthDAO() throws DataAccessException, ServiceException {
        configureDatabase();
    }

    @Override
    public String[] getCreateStatements() {
        return createStatements;
    }

    @Override
    public AuthData createAuth(String username) throws DataAccessException {
        String authToken = UUID.randomUUID().toString();

        var statement = "INSERT INTO auth (token, username) VALUES (?, ?)";
        executeUpdate(statement, authToken, username);

        return new AuthData(authToken, username);
    }

    @Override
    public AuthData getAuth(String authToken) throws DataAccessException {
        var statement = "SELECT token, username FROM auth WHERE token=?";
        try (ResultSet rs = executeQuery(statement, authToken)) {
            if (rs.next()) {
                return new AuthData(rs.getString("token"), rs.getString("username"));
            }
        } catch (SQLException ex) {
            throw new DataAccessException(ex.getMessage());
        }

        return null;
    }

    @Override
    public void deleteAuth(String authToken) throws DataAccessException {
        var statement = "DELETE FROM auth WHERE token=?";
        executeUpdate(statement, authToken);
    }

    @Override
    public void clearAuthData() throws DataAccessException {
        var statement = "TRUNCATE auth";
        executeUpdate(statement);
    }

}
