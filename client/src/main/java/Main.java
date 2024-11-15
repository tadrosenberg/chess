import chess.*;

import server.ServerFacade;
import ui.PreLoginClient;

public class Main {
    public static void main(String[] args) {
        var piece = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN);
        System.out.println("♕ 240 Chess Server: " + piece);

        ServerFacade serverFacade = new ServerFacade("http://localhost:8080"); // Update to your actual server URL
        PreLoginClient preLoginClient = new PreLoginClient(serverFacade);
        preLoginClient.start();
    }
}