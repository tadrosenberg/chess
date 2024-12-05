package server.websocket;

import chess.ChessGame;
import chess.ChessMove;
import chess.InvalidMoveException;
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
import websocket.commands.MakeMoveCommand;
import websocket.commands.UserGameCommand;
import websocket.messages.LoadGameMessage;
import websocket.messages.NotificationMessage;
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
            case MAKE_MOVE -> {
                if (command instanceof MakeMoveCommand moveCommand) {
                    handleMakeMove(moveCommand);
                } else {
                    throw new IOException("Invalid command format for MAKE_MOVE");
                }
            }
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

            String playerColor;
            if (username.equals(gameData.whiteUsername())) {
                playerColor = "WHITE";
            } else if (username.equals(gameData.blackUsername())) {
                playerColor = "BLACK";
            } else {
                playerColor = "OBSERVER";
            }

            LoadGameMessage loadGameMessage = new LoadGameMessage(
                    ServerMessage.ServerMessageType.LOAD_GAME,
                    gameData.game()
            );
            session.getRemote().sendString(new Gson().toJson(loadGameMessage));

            NotificationMessage notificationMessage = new NotificationMessage(
                    ServerMessage.ServerMessageType.NOTIFICATION,
                    username + " has joined the game as " + playerColor
            );
            connectionManager.broadcast(gameID, username, notificationMessage);

        } catch (DataAccessException e) {
            throw new IOException("Error processing connect command: " + e.getMessage());
        }
    }

    private void handleMakeMove(MakeMoveCommand command) throws IOException {
        int gameID = command.getGameID();
        String authToken = command.getAuthToken();
        ChessMove move = command.getMove(); // Extract the move from the command

        try {
            AuthData authData = authDAO.getAuth(authToken);
            if (authData == null) {
                throw new IOException("Unauthorized: Invalid auth token");
            }
            String username = authData.username();

            // Fetch the game
            GameData gameData = gameDAO.getGame(gameID);
            if (gameData == null) {
                throw new IOException("Game not found");
            }

            // Apply the move to the game
            ChessGame game = gameData.game();
            game.makeMove(move);

            // Update the game in the database
            gameDAO.updateGame(new GameData(gameData.gameID(), gameData.whiteUsername(), gameData.blackUsername(), gameData.gameName(), game));

            // Broadcast the updated game state to all clients
            LoadGameMessage loadGameMessage = new LoadGameMessage(
                    ServerMessage.ServerMessageType.LOAD_GAME,
                    game
            );
            connectionManager.broadcast(gameID, null, loadGameMessage);

            // Check for special conditions
            ChessGame.TeamColor opponentColor = game.getTeamTurn();
            if (game.isInCheck(opponentColor)) {
                String inCheckPlayer = null;
                if (opponentColor == ChessGame.TeamColor.WHITE) {
                    inCheckPlayer = gameData.whiteUsername();
                } else if (opponentColor == ChessGame.TeamColor.BLACK) {
                    inCheckPlayer = gameData.blackUsername();
                }
                NotificationMessage checkNotification = new NotificationMessage(
                        ServerMessage.ServerMessageType.NOTIFICATION,
                        inCheckPlayer + " is in check!"
                );
                connectionManager.broadcast(gameID, null, checkNotification);
            }

            if (game.isInCheckmate(opponentColor)) {
                String inCheckmatePlayer = null;
                if (opponentColor == ChessGame.TeamColor.WHITE) {
                    inCheckmatePlayer = gameData.whiteUsername();
                } else if (opponentColor == ChessGame.TeamColor.BLACK) {
                    inCheckmatePlayer = gameData.blackUsername();
                }
                NotificationMessage checkNotification = new NotificationMessage(
                        ServerMessage.ServerMessageType.NOTIFICATION,
                        inCheckmatePlayer + " is in check!"
                );
                connectionManager.broadcast(gameID, null, checkNotification);
            }

        } catch (DataAccessException ex) {
            throw new IOException("Failed to process move: " + ex.getMessage());
        } catch (InvalidMoveException ex) {
            throw new IOException("Invalid move: " + ex.getMessage());
        }
    }


    private void handleLeave(UserGameCommand command) throws IOException {
        int gameID = command.getGameID();
        String authToken = command.getAuthToken();

        try {
            AuthData authData = authDAO.getAuth(authToken);
            if (authData == null) {
                throw new IOException("Unauthorized: Invalid auth token");
            }
            String username = authData.username();


            // Remove user from the connection manager
            connectionManager.removeConnection(gameID, username);

            // Get the current game data
            GameData gameData = gameDAO.getGame(gameID);
            if (gameData == null) {
                throw new IOException("Game not found");
            }

            // Update the game state if the user was a player
            if (username.equals(gameData.whiteUsername())) {
                gameDAO.updateGame(new GameData(
                        gameID,
                        null, // White player leaves
                        gameData.blackUsername(),
                        gameData.gameName(),
                        gameData.game()
                ));
            } else if (username.equals(gameData.blackUsername())) {
                gameDAO.updateGame(new GameData(
                        gameID,
                        gameData.whiteUsername(),
                        null, // Black player leaves
                        gameData.gameName(),
                        gameData.game()
                ));
            }

            // Notify other clients
            NotificationMessage notificationMessage = new NotificationMessage(
                    ServerMessage.ServerMessageType.NOTIFICATION,
                    username + " has left the game"
            );
            connectionManager.broadcast(gameID, username, notificationMessage);

            // Clean up any closed sessions
            connectionManager.cleanUpClosedSessions();
        } catch (DataAccessException ex) {
            throw new IOException("Error processing leave command: " + ex.getMessage());
        }
    }


    private void handleResign(UserGameCommand command) throws IOException {

    }
}
