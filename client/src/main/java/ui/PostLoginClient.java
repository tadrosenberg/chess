package ui;

import exception.ServiceException;
import model.GameData;
import request.CreateGameRequest;
import result.ListGamesResult;
import server.ServerFacade;

public class PostLoginClient {

    private final ServerFacade serverFacade;
    private final String authToken;

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
        StringBuilder sb = new StringBuilder("Available games:\n");
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

    public void start() {
        PostLoginRepl postLoginRepl = new PostLoginRepl(this);
        postLoginRepl.run();
    }
}