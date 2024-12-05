package dataaccess;

import exception.ServiceException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import static dataaccess.DatabaseManager.createDatabase;
import static java.sql.Statement.RETURN_GENERATED_KEYS;
import static java.sql.Types.NULL;

public abstract class AbstractDAO {

    static {
        try {
            createDatabase();
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
    }

    protected abstract String[] getCreateStatements();

    protected void configureDatabase() throws DataAccessException, ServiceException {
        DatabaseManager.createDatabase();
        try (var conn = DatabaseManager.getConnection()) {
            for (var statement : getCreateStatements()) {
                try (var preparedStatement = conn.prepareStatement(statement)) {
                    preparedStatement.executeUpdate();
                }
            }
        } catch (SQLException ex) {
            throw new ServiceException(500, String.format("Unable to configure database: %s", ex.getMessage()));
        }
    }

    protected int executeUpdate(String statement, Object... params) throws DataAccessException {
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(statement, RETURN_GENERATED_KEYS)) {

            setParameters(ps, params);
            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
            return 0;
        } catch (SQLException e) {
            throw new DataAccessException(e.getMessage());
        }
    }

    protected ResultSet executeQuery(String statement, Object... params) throws DataAccessException {
        try {
            Connection conn = DatabaseManager.getConnection();
            PreparedStatement ps = conn.prepareStatement(statement);
            setParameters(ps, params);
            return ps.executeQuery();
        } catch (SQLException e) {
            throw new DataAccessException(e.getMessage());
        }
    }

    private void setParameters(PreparedStatement ps, Object... params) throws SQLException {
        for (int i = 0; i < params.length; i++) {
            Object param = params[i];
            if (param instanceof String) {
                ps.setString(i + 1, (String) param);
            } else if (param instanceof Integer) {
                ps.setInt(i + 1, (Integer) param);
            } else if (param instanceof Boolean) {
                ps.setBoolean(i + 1, (Boolean) param);
            } else if (param == null) {
                ps.setNull(i + 1, NULL);
            }
        }
    }
}
