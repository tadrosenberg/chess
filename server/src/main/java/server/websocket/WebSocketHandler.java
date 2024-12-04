package server.websocket;

import com.google.gson.Gson;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketClose;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketConnect;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import websocket.commands.UserGameCommand;

import java.io.IOException;

@WebSocket
public class WebSocketHandler {
    private final ConnectionManager connectionManager;

    public WebSocketHandler() {
        this.connectionManager = new ConnectionManager();
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
        String userID = command.getAuthToken(); // Assume authToken as userID for now
        connectionManager.addConnection(gameID, userID, session);
        connectionManager.broadcast(gameID, userID, userID + " has joined the game.");
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
