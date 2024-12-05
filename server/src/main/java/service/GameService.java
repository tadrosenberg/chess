package service;

import exception.ServiceException;
import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import dataaccess.GameDAO;
import model.AuthData;
import model.GameData;
import result.CreateGameResult;
import result.ListGamesResult;

public class GameService {
    private final AuthDAO authDAO;
    private final GameDAO gameDAO;

    public GameService(AuthDAO authDAO, GameDAO gameDAO) {
        this.authDAO = authDAO;
        this.gameDAO = gameDAO;
    }

    public CreateGameResult createGame(String gameName, String authToken) throws ServiceException, DataAccessException {
        if (authDAO.getAuth(authToken) == null) {
            throw new ServiceException(401, "Error: unauthorized");
        }
        if (gameName == null || gameName.isEmpty()) {
            throw new ServiceException(400, "Error: bad request");
        }

        GameData newGame = gameDAO.createGame(gameName);

        return new CreateGameResult(newGame.gameID());
    }

    public ListGamesResult listGames(String authToken) throws ServiceException, DataAccessException {
        if (authDAO.getAuth(authToken) == null) {
            throw new ServiceException(401, "Error: unauthorized");
        }

        return new ListGamesResult(gameDAO.listGames());
    }

    public void joinGame(int gameID, String playerColor, String authToken) throws ServiceException, DataAccessException {
        if (authDAO.getAuth(authToken) == null) {
            throw new ServiceException(401, "Error: unauthorized");
        }
        if (playerColor == null || playerColor.isEmpty()) {
            throw new ServiceException(400, "Error: bad request");
        }
        if (gameDAO.getGame(gameID) == null) {
            throw new ServiceException(400, "Error: bad request");
        }

        AuthData currentAuth = authDAO.getAuth(authToken);
        var joiningUser = currentAuth.username();
        GameData game = gameDAO.getGame(gameID);

        if (playerColor.equals("WHITE")) {
            if (game.whiteUsername() == null) {
                // Create a new GameData record with the updated whiteUsername
                GameData updatedGame = new GameData(game.gameID(), joiningUser, game.blackUsername(), game.gameName(), game.game(), false);
                gameDAO.updateGame(updatedGame); // Replace the old game with the updated one
            } else {
                throw new ServiceException(403, "Error: already taken");
            }
        } else if (playerColor.equals("BLACK")) {
            if (game.blackUsername() == null) {
                // Create a new GameData record with the updated blackUsername
                GameData updatedGame = new GameData(game.gameID(), game.whiteUsername(), joiningUser, game.gameName(), game.game(), false);
                gameDAO.updateGame(updatedGame); // Replace the old game with the updated one
            } else {
                throw new ServiceException(403, "Error: already taken");
            }
        }
    }
}
