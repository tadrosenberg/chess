package service;

import dataaccess.*;

public class ClearService {
    private final MemoryUserDAO USER_DAO;
    private final MemoryAuthDAO AUTH_DAO;
    private final MemoryGameDAO GAME_DAO;

    public ClearService(MemoryUserDAO userDAO, MemoryAuthDAO authDAO, MemoryGameDAO gameDAO) {
        this.USER_DAO = userDAO;
        this.AUTH_DAO = authDAO;
        this.GAME_DAO = gameDAO;
    }

    public void clear() throws DataAccessException {
        USER_DAO.clearUserData();
        AUTH_DAO.clearAuthData();
        GAME_DAO.clearGameData();
    }
}
