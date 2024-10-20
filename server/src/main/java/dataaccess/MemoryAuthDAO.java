package dataaccess;

import java.util.HashMap;
import java.util.Map;

import model.AuthData;

public class MemoryAuthDAO implements AuthDAO {
    private final Map<String, AuthData> authTokens = new HashMap<>();

    @Override
    public AuthData createAuth(AuthData authData) throws DataAccessException {
        return null;
    }

    @Override
    public AuthData getAuth(String authToken) throws DataAccessException {
        if (authTokens.containsKey(authToken)) {
            return authTokens.get(authToken);
        } else {
            throw new DataAccessException("Auth token not found");
        }
    }

    @Override
    public void deleteAuth(String authToken) throws DataAccessException {
        if (authTokens.containsKey(authToken)) {
            authTokens.remove(authToken);
        } else {
            throw new DataAccessException("Auth token not found");
        }
    }

    @Override
    public void clearAuthData() throws DataAccessException {
        authTokens.clear();
    }


}
