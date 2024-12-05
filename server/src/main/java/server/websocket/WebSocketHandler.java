package server.websocket;

import chess.ChessGame;
import chess.ChessMove;
import chess.InvalidMoveException;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
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
import websocket.messages.ErrorMessage;
import websocket.messages.LoadGameMessage;
import websocket.messages.NotificationMessage;
import websocket.messages.ServerMessage;


import java.io.IOException;

@WebSocket
public class WebSocketHandler {
    private final ConnectionManager connectionManager;
    private final AuthDAO authDAO;
    private final GameDAO gameDAO;


    public WebSocketHandler(UserDAO userDAO, AuthDAO authDAO, GameDAO gameDAO) {
        this.connectionManager = new ConnectionManager();
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
        JsonObject jsonObject = JsonParser.parseString(message).getAsJsonObject();

        String commandType = jsonObject.get("commandType").getAsString();
        System.out.println("Command type: " + commandType);

        // Switch based on commandType
        switch (commandType) {
            case "CONNECT" -> {
                UserGameCommand connectCommand = new Gson().fromJson(jsonObject, UserGameCommand.class);
                handleConnect(connectCommand, session);
            }
            case "MAKE_MOVE" -> {
                MakeMoveCommand moveCommand = new Gson().fromJson(jsonObject, MakeMoveCommand.class);
                handleMakeMove(moveCommand, session);
            }
            case "LEAVE" -> {
                UserGameCommand leaveCommand = new Gson().fromJson(jsonObject, UserGameCommand.class);
                handleLeave(leaveCommand, session);
            }
            case "RESIGN" -> {
                UserGameCommand resignCommand = new Gson().fromJson(jsonObject, UserGameCommand.class);
                handleResign(resignCommand, session);
            }
            default -> {
                System.out.println("Unknown command type: " + commandType);
                throw new IOException("Invalid command type: " + commandType);
            }
        }
    }

    private void handleConnect(UserGameCommand command, Session session) throws IOException {
        int gameID = command.getGameID();
        String authToken = command.getAuthToken();

        try {
            AuthData authData = authDAO.getAuth(authToken);
            if (authData == null) {
                sendError(session, "Unauthorized: Invalid auth token");
                return;
            }
            String username = authData.username();

            connectionManager.addConnection(gameID, username, session);

            GameData gameData = gameDAO.getGame(gameID);
            if (gameData == null) {
                sendError(session, "Game not found");
                return;
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
            ErrorMessage errorMessage = new ErrorMessage(
                    ServerMessage.ServerMessageType.ERROR,
                    "Couldn't access data"
            );
            session.getRemote().sendString(new Gson().toJson(errorMessage));
            return;
        }
    }

    private void handleMakeMove(MakeMoveCommand command, Session session) throws IOException {
        int gameID = command.getGameID();
        String authToken = command.getAuthToken();
        ChessMove move = command.getMove();

        try {
            AuthData authData = authDAO.getAuth(authToken);
            if (authData == null) {
                sendError(session, "Unauthorized: Invalid auth token");
                return;
            }
            String username = authData.username();

            // Fetch the game
            GameData gameData = gameDAO.getGame(gameID);
            if (gameData == null) {
                sendError(session, "Game not found");
                return;
            }

            // Check if the game is finished
            if (gameData.isFinished()) {
                sendError(session, "Game is already finished. No further moves allowed.");
                return;
            }

            ChessGame game = gameData.game();

            
            // Check if it's the player's turn
            ChessGame.TeamColor currentTurn = game.getTeamTurn();
            if ((currentTurn == ChessGame.TeamColor.WHITE && !username.equals(gameData.whiteUsername())) ||
                    (currentTurn == ChessGame.TeamColor.BLACK && !username.equals(gameData.blackUsername()))) {
                ErrorMessage errorMessage = new ErrorMessage(
                        ServerMessage.ServerMessageType.ERROR,
                        "Not your turn!"
                );
                session.getRemote().sendString(new Gson().toJson(errorMessage));
                return;
            }

            // Check if the piece belongs to the player
            ChessGame.TeamColor pieceColor = game.getBoard().getPiece(move.getStartPosition()).getTeamColor();
            if ((currentTurn == ChessGame.TeamColor.WHITE && pieceColor != ChessGame.TeamColor.WHITE) ||
                    (currentTurn == ChessGame.TeamColor.BLACK && pieceColor != ChessGame.TeamColor.BLACK)) {
                ErrorMessage errorMessage = new ErrorMessage(
                        ServerMessage.ServerMessageType.ERROR,
                        "Only move your own piece!"
                );
                session.getRemote().sendString(new Gson().toJson(errorMessage));
                return;
            }

            try {
                game.makeMove(move);
            } catch (InvalidMoveException ex) {
                ErrorMessage errorMessage = new ErrorMessage(
                        ServerMessage.ServerMessageType.ERROR,
                        "Invalid move"
                );
                session.getRemote().sendString(new Gson().toJson(errorMessage));
                return;
            }

            // Update the game in the database
            gameDAO.updateGame(new GameData(gameData.gameID(), gameData.whiteUsername(), gameData.blackUsername(), gameData.gameName(), game, false));

            NotificationMessage notificationMessage = new NotificationMessage(
                    ServerMessage.ServerMessageType.NOTIFICATION,
                    username + " has made the move: " + move
            );
            connectionManager.broadcast(gameID, username, notificationMessage);

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
        }
    }


    private void handleLeave(UserGameCommand command, Session session) throws IOException {
        int gameID = command.getGameID();
        String authToken = command.getAuthToken();

        try {
            AuthData authData = authDAO.getAuth(authToken);
            if (authData == null) {
                sendError(session, "Unauthorized: Invalid auth token");
                return;
            }
            String username = authData.username();


            // Remove user from the connection manager
            connectionManager.removeConnection(gameID, username);

            // Get the current game data
            GameData gameData = gameDAO.getGame(gameID);
            if (gameData == null) {
                sendError(session, "Game not found");
                return;
            }

            // Update the game state if the user was a player
            if (username.equals(gameData.whiteUsername())) {
                gameDAO.updateGame(new GameData(
                        gameID,
                        null, // White player leaves
                        gameData.blackUsername(),
                        gameData.gameName(),
                        gameData.game(),
                        false
                ));
            } else if (username.equals(gameData.blackUsername())) {
                gameDAO.updateGame(new GameData(
                        gameID,
                        gameData.whiteUsername(),
                        null, // Black player leaves
                        gameData.gameName(),
                        gameData.game(),
                        false
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
            ErrorMessage errorMessage = new ErrorMessage(
                    ServerMessage.ServerMessageType.ERROR,
                    "Couldn't process leave command"
            );
            session.getRemote().sendString(new Gson().toJson(errorMessage));
        }
    }


    private void handleResign(UserGameCommand command, Session session) throws IOException {
        int gameID = command.getGameID();
        String authToken = command.getAuthToken();

        try {
            // Validate the auth token
            AuthData authData = authDAO.getAuth(authToken);
            if (authData == null) {
                sendError(session, "Unauthorized: Invalid auth token");
                return;
            }
            String username = authData.username();

            // Get the current game data
            GameData gameData = gameDAO.getGame(gameID);
            if (gameData == null) {
                sendError(session, "Game not found");
                return;
            }

            ChessGame game = gameData.game();

            // Determine if the user is a player in the game
            if (username.equals(gameData.whiteUsername())) {
                // White player resigns
                NotificationMessage resignNotification = new NotificationMessage(
                        ServerMessage.ServerMessageType.NOTIFICATION,
                        "White player " + username + " has resigned. Black wins!"
                );
                connectionManager.broadcast(gameID, null, resignNotification);

                // Update game as finished
                gameDAO.updateGame(new GameData(
                        gameID,
                        gameData.whiteUsername(),
                        gameData.blackUsername(),
                        gameData.gameName(),
                        game,
                        true // Mark game as finished
                ));
            } else if (username.equals(gameData.blackUsername())) {
                // Black player resigns
                NotificationMessage resignNotification = new NotificationMessage(
                        ServerMessage.ServerMessageType.NOTIFICATION,
                        "Black player " + username + " has resigned. White wins!"
                );
                connectionManager.broadcast(gameID, null, resignNotification);

                // Update game as finished
                gameDAO.updateGame(new GameData(
                        gameID,
                        gameData.whiteUsername(),
                        gameData.blackUsername(),
                        gameData.gameName(),
                        game,
                        true // Mark game as finished
                ));
            } else {
                // User is not a player in the game
                sendError(session, "Only players can resign from the game");
                return;
            }

        } catch (DataAccessException ex) {
            sendError(session, "Failed to process resign: " + ex.getMessage());
        }
    }


    private void sendError(Session session, String errorMessageText) throws IOException {
        ErrorMessage errorMessage = new ErrorMessage(
                ServerMessage.ServerMessageType.ERROR,
                errorMessageText
        );
        session.getRemote().sendString(new Gson().toJson(errorMessage));
    }

}
