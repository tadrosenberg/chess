package dataaccess;

import chess.ChessGame;
import model.GameData;
import service.ServiceException;

import java.util.HashMap;
import java.util.Map;

public class MemoryGameDAO implements GameDAO {
    private final Map<Integer, GameData> games = new HashMap<>();
    private int nextGameID = 1;

    @Override
    public GameData createGame(String gameName) throws DataAccessException {
        int gameID = nextGameID++;

        ChessGame newChessGame = new ChessGame();
        GameData newGame = new GameData(gameID, null, null, gameName, newChessGame);

        games.put(gameID, newGame);

        return games.get(gameID);
    }

    @Override
    public GameData getGame(int gameID) {
        return games.get(gameID);
    }

    @Override
    public void updateGame(GameData newGame) {
        games.put(newGame.gameID(), newGame);
    }

    @Override
    public GameData[] listGames() throws DataAccessException {
        return games.values().toArray(new GameData[0]);
    }

    @Override
    public void clearGameData() throws DataAccessException {
        games.clear();
        nextGameID = 1;
    }
}
