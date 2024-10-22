package service;

import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import dataaccess.GameDAO;
import model.AuthData;
import model.GameData;
import result.CreateGameResult;
import result.ListGamesResult;

public class GameService {
    private final AuthDAO AUTH_DAO;
    private final GameDAO GAME_DAO;

    public GameService(AuthDAO authDAO, GameDAO gameDAO) {
        this.AUTH_DAO = authDAO;
        this.GAME_DAO = gameDAO;
    }

    public CreateGameResult createGame(String gameName, String authToken) throws ServiceException, DataAccessException {
        if (AUTH_DAO.getAuth(authToken) == null) {
            throw new ServiceException(401, "Error: unauthorized");
        }
        if (gameName == null || gameName.isEmpty()) {
            throw new ServiceException(400, "Error: bad request");
        }
        GameData newGame = GAME_DAO.createGame(gameName);

        return new CreateGameResult(newGame.gameID());
    }

    public ListGamesResult listGames(String authToken) throws ServiceException, DataAccessException {
        if (AUTH_DAO.getAuth(authToken) == null) {
            throw new ServiceException(401, "Error: unauthorized");
        }

        return new ListGamesResult(GAME_DAO.listGames());
    }

    public void joinGame(int gameID, String playerColor, String authToken) throws ServiceException, DataAccessException {
        if (AUTH_DAO.getAuth(authToken) == null) {
            throw new ServiceException(401, "Error: unauthorized");
        }
        if (playerColor == null || playerColor.isEmpty()) {
            throw new ServiceException(400, "Error: bad request");
        }
        if (GAME_DAO.getGame(gameID) == null) {
            throw new ServiceException(400, "Error: bad request");
        }

        AuthData currentAuth = AUTH_DAO.getAuth(authToken);
        var joiningUser = currentAuth.username();
        GameData game = GAME_DAO.getGame(gameID);

        if (playerColor.equals("WHITE")) {
            if (game.whiteUsername() == null) {
                // Create a new GameData record with the updated whiteUsername
                GameData updatedGame = new GameData(game.gameID(), joiningUser, game.blackUsername(), game.gameName(), game.game());
                GAME_DAO.updateGame(updatedGame); // Replace the old game with the updated one
            } else {
                throw new ServiceException(403, "Error: already taken");
            }
        } else if (playerColor.equals("BLACK")) {
            if (game.blackUsername() == null) {
                // Create a new GameData record with the updated blackUsername
                GameData updatedGame = new GameData(game.gameID(), game.whiteUsername(), joiningUser, game.gameName(), game.game());
                GAME_DAO.updateGame(updatedGame); // Replace the old game with the updated one
            } else {
                throw new ServiceException(403, "Error: already taken");
            }
        }
    }
}
