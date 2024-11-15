package ui;

import server.ServerFacade;

public class PostLoginClient {

    private final ServerFacade serverFacade;
    private final String authToken;

    public PostLoginClient(ServerFacade serverFacade, String authToken) {
        this.serverFacade = serverFacade;
        this.authToken = authToken;

    }

    public void start() {
        System.out.println("Post-login client is not yet implemented.");
    }
}