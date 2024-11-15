package server;

import com.google.gson.Gson;
import exception.ServiceException;
import model.UserData;
import model.AuthData;
import model.GameData;
import request.CreateGameRequest;
import request.JoinGameRequest;
import result.CreateGameResult;
import result.ListGamesResult;
import result.LoginResult;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;

public class ServerFacade {
    private final String serverUrl;
    private final Gson gson = new Gson();

    public ServerFacade(String serverUrl) {
        this.serverUrl = serverUrl;
    }

    public AuthData register(UserData user) throws ServiceException {
        return makeRequest("POST", "/user", user, AuthData.class);
    }

    public LoginResult login(UserData user) throws ServiceException {
        return makeRequest("POST", "/session", user, LoginResult.class);
    }

    public void logout(String authToken) throws ServiceException {
        makeRequest("DELETE", "/session", null, null, authToken);
    }

    public CreateGameResult createGame(CreateGameRequest request, String authToken) throws ServiceException {
        return makeRequest("POST", "/game", request, CreateGameResult.class, authToken);
    }

    public ListGamesResult listGames(String authToken) throws ServiceException {
        return makeRequest("GET", "/game", null, ListGamesResult.class, authToken);
    }

    public void joinGame(JoinGameRequest request, String authToken) throws ServiceException {
        makeRequest("PUT", "/game", request, null, authToken);
    }

    public void clearDatabase() throws ServiceException {
        makeRequest("DELETE", "/db", null, null); // Adjust as needed if your clear endpoint differs
    }

    private <T> T makeRequest(String method, String path, Object request, Class<T> responseClass) throws ServiceException {
        return makeRequest(method, path, request, responseClass, null); // Call overloaded method with no authToken
    }

    private <T> T makeRequest(String method, String path, Object request, Class<T> responseClass, String authToken) throws ServiceException {
        try {
            URL url = (new URI(serverUrl + path)).toURL();
            HttpURLConnection http = (HttpURLConnection) url.openConnection();
            http.setRequestMethod(method);
            http.setDoOutput(true);

            // Add authorization header if authToken is provided
            if (authToken != null && !authToken.isEmpty()) {
                http.setRequestProperty("Authorization", authToken);
            }

            writeBody(request, http);
            http.connect();
            throwIfNotSuccessful(http);
            return readBody(http, responseClass);
        } catch (Exception ex) {
            throw new ServiceException(500, ex.getMessage());
        }
    }


    private static void writeBody(Object request, HttpURLConnection http) throws IOException {
        if (request != null) {
            http.addRequestProperty("Content-Type", "application/json");
            String reqData = new Gson().toJson(request);
            try (OutputStream reqBody = http.getOutputStream()) {
                reqBody.write(reqData.getBytes());
            }
        }
    }

    private void throwIfNotSuccessful(HttpURLConnection http) throws IOException, ServiceException {
        var status = http.getResponseCode();
        if (!isSuccessful(status)) {
            throw new ServiceException(status, "failure: " + status);
        }
    }

    private static <T> T readBody(HttpURLConnection http, Class<T> responseClass) throws IOException {
        T response = null;
        if (http.getContentLength() < 0) {
            try (InputStream respBody = http.getInputStream()) {
                InputStreamReader reader = new InputStreamReader(respBody);
                if (responseClass != null) {
                    response = new Gson().fromJson(reader, responseClass);
                }
            }
        }
        return response;
    }

    private boolean isSuccessful(int status) {
        return status / 100 == 2;
    }
}
