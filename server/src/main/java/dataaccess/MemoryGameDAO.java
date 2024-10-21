package dataaccess;

import model.GameData;

import java.util.HashMap;
import java.util.Map;

public class MemoryGameDAO implements GameDAO {
    private final Map<Integer, GameData> games = new HashMap<>();
    private int nextGameId = 1;

    @Override
    public GameData createGame(String gameName) throws DataAccessException {
        int gameId = nextGameId++;

        GameData newGame = new GameData(gameId, null, null, gameName, null);

        games.put(gameId, newGame);

        return games.get(gameId);
    }

    @Override
    public GameData getGame(int gameID) throws DataAccessException {
        if (!games.containsKey(gameID)) {
            throw new DataAccessException("User not found.");
        }
        return games.get(gameID);
    }

    @Override
    public GameData updateGame(GameData newGame) throws DataAccessException {
        return null;
    }

    @Override
    public GameData[] listGames() throws DataAccessException {
        return games.values().toArray(new GameData[0]);
    }

    @Override
    public void clearGameData() throws DataAccessException {
        games.clear();
        nextGameId = 1;
    }
}
