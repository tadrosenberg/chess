package dataaccess;

import model.GameData;

public class SQLGameDAO implements GameDAO {
    @Override
    public GameData createGame(String gameName) throws DataAccessException {
        return null;
    }

    @Override
    public GameData getGame(int gameID) {
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
}
