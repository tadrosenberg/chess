package service;

import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import dataaccess.GameDAO;
import dataaccess.UserDAO;
import model.GameData;
import request.CreateGameRequest;
import result.CreateGameResult;

public class GameService {
    private final UserDAO userDAO;
    private final AuthDAO authDAO;
    private final GameDAO gameDAO;

    public GameService(UserDAO userDAO, AuthDAO authDAO, GameDAO gameDAO) {
        this.userDAO = userDAO;
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

        return new CreateGameResult(newGame.gameId());
    }
}
