package dataaccess;

import chess.ChessGame;
import com.google.gson.Gson;
import model.GameData;
import exception.ServiceException;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class SQLGameDAO extends AbstractDAO implements GameDAO {
    private final String[] createStatements = {
            """
            CREATE TABLE IF NOT EXISTS  game (
              `gameID` INT NOT NULL AUTO_INCREMENT,
              `whiteUsername` varchar(256) DEFAULT NULL,
              `blackUsername` varchar(256) DEFAULT NULL,
              `gameName` varchar(256) NOT NULL,
              `gameJson` TEXT DEFAULT NULL,
              `isFinished` BOOLEAN DEFAULT FALSE,
              PRIMARY KEY (`gameID`)
            ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci
            """
    };

    public SQLGameDAO() throws ServiceException, DataAccessException {
        configureDatabase();
    }

    @Override
    public GameData createGame(String gameName) throws DataAccessException {
        ChessGame game = new ChessGame();
        String gameJson = serializeChessGame(game);
        String statement = "INSERT INTO game (gameName, gameJson, isFinished) VALUES (?, ?, ?)";
        int id = executeUpdate(statement, gameName, gameJson, false);
        return new GameData(id, null, null, gameName, game, false);
    }

    private String serializeChessGame(ChessGame chessGame) throws DataAccessException {
        try {
            return new Gson().toJson(chessGame);
        } catch (Exception e) {
            throw new DataAccessException("Error serializing ChessGame to JSON");
        }
    }

    @Override
    public GameData getGame(int gameID) throws DataAccessException {
        var statement = "SELECT gameID, whiteUsername, blackUsername, gameName, gameJson, isFinished FROM game WHERE gameID=?";
        try (ResultSet rs = executeQuery(statement, gameID)) {
            if (rs.next()) {
                var json = rs.getString("gameJson");
                var game = new Gson().fromJson(json, ChessGame.class);
                return new GameData(rs.getInt("gameID"), rs.getString("whiteUsername"),
                        rs.getString("blackUsername"), rs.getString("gameName"), game, rs.getBoolean("isFinished"));
            }
        } catch (SQLException ex) {
            throw new DataAccessException(ex.getMessage());
        }

        return null;
    }

    @Override
    public void updateGame(GameData newGame) throws DataAccessException {
        var statement = "UPDATE game SET whiteUsername = ?, blackUsername = ?, gameName = ?, gameJson = ?, isFinished = ? WHERE gameID = ?";
        var json = new Gson().toJson(newGame.game());
        executeUpdate(statement, newGame.whiteUsername(), newGame.blackUsername(), newGame.gameName(), json, newGame.isFinished(), newGame.gameID());
    }

    @Override
    public GameData[] listGames() throws DataAccessException {
        var statement = "SELECT gameID, whiteUsername, blackUsername, gameName, gameJson, isFinished FROM game";
        List<GameData> games = new ArrayList<>();

        try (ResultSet rs = executeQuery(statement)) { // Execute the query to get all games
            while (rs.next()) {
                var json = rs.getString("gameJson");
                var serGame = new Gson().fromJson(json, ChessGame.class);
                GameData game = new GameData(
                        rs.getInt("gameID"),
                        rs.getString("whiteUsername"),
                        rs.getString("blackUsername"),
                        rs.getString("gameName"),
                        serGame,
                        rs.getBoolean("isFinished")
                );
                games.add(game);
            }
        } catch (SQLException e) {
            throw new DataAccessException(e.getMessage());
        }

        return games.toArray(new GameData[0]);
    }

    @Override
    public void clearGameData() throws DataAccessException {
        var statement = "TRUNCATE game";
        executeUpdate(statement);
    }

    @Override
    protected String[] getCreateStatements() {
        return createStatements;
    }
}
