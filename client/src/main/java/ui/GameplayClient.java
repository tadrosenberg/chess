package ui;

import chess.ChessMove;
import exception.ServiceException;
import websocket.commands.MakeMoveCommand;
import websocket.commands.UserGameCommand;
import websocket.messages.ServerMessage;

public class GameplayClient implements ServerMessageObserver {
    private final WebSocketFacade webSocketFacade;
    private final int gameID;
    private final String authToken;

    public GameplayClient(WebSocketFacade webSocketFacade, int gameID, String authToken) {
        this.webSocketFacade = webSocketFacade;
        this.gameID = gameID;
        this.authToken = authToken;
    }

    public void connect() throws ServiceException {
        var connectCommand = new UserGameCommand(UserGameCommand.CommandType.CONNECT, authToken, gameID);
        webSocketFacade.sendCommand(connectCommand);
    }

    public void makeMove(ChessMove move) throws ServiceException {
        MakeMoveCommand moveCommand = new MakeMoveCommand(authToken, gameID, move);
        webSocketFacade.sendCommand(moveCommand);
    }

    public void leaveGame() throws ServiceException {
        var leaveCommand = new UserGameCommand(UserGameCommand.CommandType.LEAVE, authToken, gameID);
        webSocketFacade.sendCommand(leaveCommand);
        webSocketFacade.close();
    }

    public void resignGame() throws ServiceException {
        var resignCommand = new UserGameCommand(UserGameCommand.CommandType.RESIGN, authToken, gameID);
        webSocketFacade.sendCommand(resignCommand);
    }

    @Override
    public void notify(ServerMessage message) {

    }
}
