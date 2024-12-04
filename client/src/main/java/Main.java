import chess.*;

import server.ServerFacade;
import ui.PreLoginClient;

public class Main {
    public static void main(String[] args) {
        ServerFacade serverFacade = new ServerFacade("http://localhost:8080");
        PreLoginClient preLoginClient = new PreLoginClient(serverFacade);
        preLoginClient.start();
    }
}