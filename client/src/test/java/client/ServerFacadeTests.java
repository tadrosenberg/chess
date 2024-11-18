package client;

import model.UserData;
import model.AuthData;
import request.CreateGameRequest;
import request.JoinGameRequest;
import result.CreateGameResult;
import result.ListGamesResult;
import result.LoginResult;
import exception.ServiceException;
import org.junit.jupiter.api.*;
import server.Server;
import server.ServerFacade;

import static org.junit.jupiter.api.Assertions.*;

public class ServerFacadeTests {

    private static Server server;
    private static ServerFacade serverFacade;

    private static final String VALID_USERNAME = "validUser";
    private static final String VALID_PASSWORD = "validPassword";
    private static final String VALID_EMAIL = "valid@example.com";
    private static final String INVALID_AUTH_TOKEN = "invalidToken";

    @BeforeAll
    public static void init() {
        server = new Server();
        var port = server.run(0);
        System.out.println("Started test HTTP server on " + port);

        serverFacade = new ServerFacade("http://localhost:" + port);
    }

    @BeforeEach
    public void setup() throws ServiceException {
        serverFacade.clearDatabase();
    }

    @AfterAll
    static void stopServer() {
        server.stop();
    }

    @Test
    public void testRegisterSuccess() throws ServiceException {
        UserData newUser = new UserData(VALID_USERNAME, VALID_PASSWORD, VALID_EMAIL);
        AuthData authData = serverFacade.register(newUser);

        assertNotNull(authData);
        assertEquals(VALID_USERNAME, authData.username());
    }

    @Test
    public void testRegisterFailure() {
        UserData newUser = new UserData("", VALID_PASSWORD, VALID_EMAIL);
        assertThrows(ServiceException.class, () -> serverFacade.register(newUser));
    }

    @Test
    public void testLoginSuccess() throws ServiceException {
        UserData newUser = new UserData(VALID_USERNAME, VALID_PASSWORD, VALID_EMAIL);
        serverFacade.register(newUser);

        LoginResult loginResult = serverFacade.login(newUser);
        assertNotNull(loginResult);
        assertEquals(VALID_USERNAME, loginResult.username());
        assertNotNull(loginResult.authToken());
    }

    @Test
    public void testLoginFailure() {
        UserData invalidUser = new UserData("nonExistentUser", VALID_PASSWORD, VALID_EMAIL);
        assertThrows(ServiceException.class, () -> serverFacade.login(invalidUser));
    }

    @Test
    public void testLogoutSuccess() throws ServiceException {
        UserData newUser = new UserData(VALID_USERNAME, VALID_PASSWORD, VALID_EMAIL);
        AuthData authData = serverFacade.register(newUser);

        assertDoesNotThrow(() -> serverFacade.logout(authData.authToken()));
    }

    @Test
    public void testLogoutFailure() {
        assertThrows(ServiceException.class, () -> serverFacade.logout(INVALID_AUTH_TOKEN));
    }

    @Test
    public void testCreateGameSuccess() throws ServiceException {
        UserData newUser = new UserData(VALID_USERNAME, VALID_PASSWORD, VALID_EMAIL);
        AuthData authData = serverFacade.register(newUser);

        CreateGameRequest request = new CreateGameRequest("TestGame");
        CreateGameResult createGameResult = serverFacade.createGame(request, authData.authToken());

        assertNotNull(createGameResult);
        assertTrue(createGameResult.gameID() > 0);
    }

    @Test
    public void testCreateGameFailure() {
        CreateGameRequest request = new CreateGameRequest("TestGame");
        assertThrows(ServiceException.class, () -> serverFacade.createGame(request, INVALID_AUTH_TOKEN));
    }

    @Test
    public void testListGamesSuccess() throws ServiceException {
        UserData newUser = new UserData(VALID_USERNAME, VALID_PASSWORD, VALID_EMAIL);
        AuthData authData = serverFacade.register(newUser);

        ListGamesResult listGamesResult = serverFacade.listGames(authData.authToken());
        assertNotNull(listGamesResult);
        assertTrue(listGamesResult.games() != null);
    }

    @Test
    public void testListGamesFailure() {
        assertThrows(ServiceException.class, () -> serverFacade.listGames(INVALID_AUTH_TOKEN));
    }

    @Test
    public void testJoinGameSuccess() throws ServiceException {
        UserData newUser = new UserData(VALID_USERNAME, VALID_PASSWORD, VALID_EMAIL);
        AuthData authData = serverFacade.register(newUser);

        CreateGameRequest createRequest = new CreateGameRequest("TestGame");
        CreateGameResult createGameResult = serverFacade.createGame(createRequest, authData.authToken());

        JoinGameRequest joinRequest = new JoinGameRequest("WHITE", createGameResult.gameID());
        assertDoesNotThrow(() -> serverFacade.joinGame(joinRequest, authData.authToken()));
    }

    @Test
    public void testJoinGameFailure() {
        JoinGameRequest joinRequest = new JoinGameRequest("WHITE", 9999);
        assertThrows(ServiceException.class, () -> serverFacade.joinGame(joinRequest, INVALID_AUTH_TOKEN));
    }
}
