package server.websocket;

import com.google.gson.Gson;
import org.eclipse.jetty.websocket.api.Session;
import websocket.messages.ServerMessage;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class ConnectionManager {
    private final Map<Integer, Set<Connection>> gameSessions = new ConcurrentHashMap<>();

    public void addConnection(int gameID, String username, Session session) {
        var connection = new Connection(username, session);
        gameSessions.computeIfAbsent(gameID, k -> new HashSet<>()).add(connection);
    }

    public void removeConnection(int gameID, String username) {
        var connections = gameSessions.get(gameID);
        if (connections != null) {
            connections.removeIf(conn -> conn.username.equals(username));
            if (connections.isEmpty()) {
                gameSessions.remove(gameID);
            }
        }
    }

    public void broadcast(int gameID, String excludeUsername, ServerMessage serverMessage) throws IOException {
        var connections = gameSessions.get(gameID);
        if (connections != null) {
            for (var connection : connections) {
                if (!connection.username.equals(excludeUsername) && connection.session.isOpen()) {
                    connection.send(new Gson().toJson(serverMessage));
                }
            }
        }
    }

    public void cleanUpClosedSessions() {
        for (var connections : gameSessions.values()) {
            connections.removeIf(conn -> !conn.session.isOpen());
        }
    }
}
