package dataaccess;

import model.GameData;

public interface GameDAO {
    GameData createGame(String gameName) throws DataAccessException;

    GameData getGame(int gameID) throws DataAccessException;

    GameData updateGame(GameData newGame) throws DataAccessException;

    GameData[] listGames() throws DataAccessException;

    void clearGameData() throws DataAccessException;
}
