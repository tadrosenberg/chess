package ui;

import exception.ServiceException;
import model.GameData;
import request.CreateGameRequest;
import request.JoinGameRequest;
import result.ListGamesResult;
import server.ServerFacade;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class PostLoginClient {

    private final ServerFacade serverFacade;
    private final String authToken;
    private List<GameData> gamesList;

    public PostLoginClient(ServerFacade serverFacade, String authToken) {
        this.serverFacade = serverFacade;
        this.authToken = authToken;
    }

    public void create(String name) throws ServiceException {
        CreateGameRequest game = new CreateGameRequest(name);
        serverFacade.createGame(game, authToken);
    }

    public String list() throws ServiceException {
        ListGamesResult gamesResult = serverFacade.listGames(authToken);
        gamesList = Arrays.asList(gamesResult.games());

        StringBuilder sb = new StringBuilder();
        int count = 1;
        for (GameData game : gamesResult.games()) {
            sb.append(count++).append(". ")
                    .append(game.gameName())
                    .append(" - White: ").append(game.whiteUsername() != null ? game.whiteUsername() : "TBD")
                    .append(", Black: ").append(game.blackUsername() != null ? game.blackUsername() : "TBD")
                    .append("\n");
        }
        return sb.toString();
    }

    public void logout() throws ServiceException {
        serverFacade.logout(authToken);
    }

    public void observe(int gameNumber) throws ServiceException {
        if (getGameByNumber(gameNumber) != null) {
            System.out.println("printing board");
        } else {
            throw new ServiceException(401, "game not found");
        }
    }

    public void join(int gameNumber, String playerColor) throws ServiceException {
        if (getGameByNumber(gameNumber) != null || !Objects.equals(playerColor, "WHITE") || !Objects.equals(playerColor, "BLACK")) {
            JoinGameRequest joinGameRequest = new JoinGameRequest(playerColor, getGameByNumber(gameNumber).gameID());
            serverFacade.joinGame(joinGameRequest, authToken);
            System.out.println("printing board");
        } else {
            throw new ServiceException(401, "game not found");
        }
    }

    private GameData getGameByNumber(int gameNumber) {
        if (gameNumber > 0 && gameNumber <= gamesList.size()) {
            return gamesList.get(gameNumber - 1);
        }
        return null;
    }

    public void start() {
        PostLoginRepl postLoginRepl = new PostLoginRepl(this);
        boolean loggedOut = postLoginRepl.run();
        if (loggedOut) {
            System.out.println("You have been logged out.");
        }
    }
}