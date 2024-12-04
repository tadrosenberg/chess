package server.websocket;

import com.google.gson.Gson;
import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import dataaccess.GameDAO;
import dataaccess.UserDAO;
import model.AuthData;
import model.GameData;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketClose;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketConnect;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import websocket.commands.UserGameCommand;
import websocket.messages.LoadGameMessage;
import websocket.messages.ServerMessage;


import java.io.IOException;

@WebSocket
public class WebSocketHandler {
    private final ConnectionManager connectionManager;
    private final UserDAO userDAO;
    private final AuthDAO authDAO;
    private final GameDAO gameDAO;


    public WebSocketHandler(UserDAO userDAO, AuthDAO authDAO, GameDAO gameDAO) {
        this.connectionManager = new ConnectionManager();
        this.userDAO = userDAO;
        this.authDAO = authDAO;
        this.gameDAO = gameDAO;
    }

    @OnWebSocketConnect
    public void onConnect(Session session) {
        System.out.println("WebSocket connection opened: " + session);
    }

    @OnWebSocketClose
    public void onClose(Session session, int statusCode, String reason) {
        System.out.println("WebSocket connection closed: " + session);
        connectionManager.cleanUpClosedSessions();
    }

    @OnWebSocketMessage
    public void onMessage(Session session, String message) throws IOException {
        var command = new Gson().fromJson(message, UserGameCommand.class);
        switch (command.getCommandType()) {
            case CONNECT -> handleConnect(command, session);
            case MAKE_MOVE -> handleMakeMove(command);
            case LEAVE -> handleLeave(command);
            case RESIGN -> handleResign(command);
            default -> System.out.println("Unknown command type: " + command.getCommandType());
        }
    }

    private void handleConnect(UserGameCommand command, Session session) throws IOException {
        int gameID = command.getGameID();
        String authToken = command.getAuthToken();

        try {
            AuthData authData = authDAO.getAuth(authToken);
            if (authData == null) {
                throw new IOException("Unauthorized: Invalid auth token");
            }
            String username = authData.username();

            connectionManager.addConnection(gameID, username, session);

            GameData gameData = gameDAO.getGame(gameID);
            if (gameData == null) {
                throw new IOException("Game not found");
            }

            // Send LOAD_GAME to Root Client
            LoadGameMessage loadGameMessage = new LoadGameMessage(
                    ServerMessage.ServerMessageType.LOAD_GAME,
                    gameData.game()
            );
            session.getRemote().sendString(new Gson().toJson(loadGameMessage));

            // Send Notification to Other Clients
            connectionManager.broadcast(gameID, username, username + " has joined the game.");

        } catch (DataAccessException e) {
            throw new IOException("Error processing connect command: " + e.getMessage());
        }
    }

    private void handleMakeMove(UserGameCommand command) throws IOException {
        int gameID = command.getGameID();
        connectionManager.broadcast(gameID, "", "Move made: ");
    }

    private void handleLeave(UserGameCommand command) throws IOException {
        int gameID = command.getGameID();
        String userID = command.getAuthToken();
        connectionManager.removeConnection(gameID, userID);
        connectionManager.broadcast(gameID, userID, userID + " has left the game.");
    }

    private void handleResign(UserGameCommand command) throws IOException {
        int gameID = command.getGameID();
        connectionManager.broadcast(gameID, "", "Game resigned by: " + command.getAuthToken());
    }
}
