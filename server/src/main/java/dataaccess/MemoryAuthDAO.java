package dataaccess;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import model.AuthData;

public class MemoryAuthDAO implements AuthDAO {
    private final Map<String, AuthData> authTokens = new HashMap<>();

    @Override
    public AuthData createAuth(String username) {
        // Generate a new, random auth token
        String authToken = UUID.randomUUID().toString();

        // Create a new AuthData object with the generated token and user data
        AuthData newAuthData = new AuthData(authToken, username);

        // Store the new auth token in the map
        authTokens.put(authToken, newAuthData);

        // Return the newly created AuthData with the token
        return newAuthData;
    }

    @Override
    public AuthData getAuth(String authToken) {
        return authTokens.get(authToken);
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
