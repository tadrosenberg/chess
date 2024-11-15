package ui;

import exception.ServiceException;
import model.UserData;
import result.LoginResult;
import server.ServerFacade;

public class PreLoginClient {

    private final ServerFacade serverFacade;
    private String authToken;

    public PreLoginClient(ServerFacade serverFacade) {
        this.serverFacade = serverFacade;
    }

    public void register(String username, String password, String email) throws ServiceException {
        UserData user = new UserData(username, password, email);
        serverFacade.register(user);
    }

    public LoginResult login(String username, String password) throws ServiceException {
        UserData user = new UserData(username, password, null);
        LoginResult loginResult = serverFacade.login(user);
        this.authToken = loginResult.authToken();
        return loginResult;
    }

    public void start() {
        PreLoginRepl preLoginRepl = new PreLoginRepl(this);
        preLoginRepl.run();

        PostLoginClient postLoginClient = new PostLoginClient(serverFacade, authToken);
        postLoginClient.start();
    }
}
