package websocket.messages;

import chess.ChessGame;

public class LoadGameMessage extends ServerMessage {
    private final ChessGame game;

    public LoadGameMessage(ServerMessageType type, ChessGame game) {
        super(type); // Initialize the parent ServerMessage with the type
        this.game = game; // Store the game state
    }

    public ChessGame getGame() {
        return game;
    }
}
