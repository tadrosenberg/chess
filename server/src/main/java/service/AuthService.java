package service;

import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import model.AuthData;

public class AuthService {
    private final AuthDAO AUTH_DAO;

    public AuthService(AuthDAO authDAO) {
        this.AUTH_DAO = authDAO;
    }

    public AuthData createAuth(String username) throws DataAccessException {
        return AUTH_DAO.createAuth(username);
    }

    public AuthData getAuth(String authToken) throws DataAccessException {
        return AUTH_DAO.getAuth(authToken);
    }

    public void deleteAuth(String authToken) throws DataAccessException {
        AUTH_DAO.deleteAuth(authToken);
    }
}
