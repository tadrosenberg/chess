package dataaccess;

import chess.ChessGame;
import com.google.gson.Gson;
import model.AuthData;
import model.GameData;
import service.ServiceException;

import java.sql.ResultSet;
import java.sql.SQLException;

public class SQLGameDAO extends AbstractDAO implements GameDAO {
    private final String[] createStatements = {
            """
            CREATE TABLE IF NOT EXISTS  game (
              `gameID` INT NOT NULL AUTO_INCREMENT,
              `whiteUsername` varchar(256) DEFAULT NULL,
              `blackUsername` varchar(256) DEFAULT NULL,
              `gameName` varchar(256) NOT NULL,
              `gameJson` TEXT DEFAULT NULL,
              PRIMARY KEY (`gameID`),
              INDEX(username)
            ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci
            """
    };

    public SQLGameDAO() throws ServiceException, DataAccessException {
        configureDatabase();
    }

    @Override
    public GameData createGame(String gameName) throws DataAccessException {
        var statement = "INSERT INTO game (gameName) VALUES (?)";
        int id = executeUpdate(statement, gameName);
        return new GameData(id, null, null, gameName, null);
    }

    @Override
    public GameData getGame(int gameID) throws DataAccessException {
        var statement = "SELECT gameID, whiteUsername, blackUsername, gameName, gameJson FROM game WHERE gameID=?";
        try (ResultSet rs = executeQuery(statement, gameID)) {
            if (rs.next()) {
                var json = rs.getString("gameJson");
                var game = new Gson().fromJson(json, ChessGame.class);
                return new GameData(rs.getInt("gameID"), rs.getString("whiteUsername"),
                        rs.getString("blackUsername"), rs.getString("gameName"), game);
            }
        } catch (SQLException ex) {
            throw new DataAccessException(ex.getMessage());
        }

        return null;
    }

    @Override
    public void updateGame(GameData newGame) {
        
    }

    @Override
    public GameData[] listGames() throws DataAccessException {
        return new GameData[0];
    }

    @Override
    public void clearGameData() throws DataAccessException {

    }

    @Override
    protected String[] getCreateStatements() {
        return createStatements;
    }
}
