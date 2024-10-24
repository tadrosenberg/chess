package dataaccess;

import model.GameData;

public interface GameDAO {
    GameData createGame(String gameName) throws DataAccessException;

    GameData getGame(int gameID);

    void updateGame(GameData newGame);


    GameData[] listGames() throws DataAccessException;

    void clearGameData() throws DataAccessException;
}
