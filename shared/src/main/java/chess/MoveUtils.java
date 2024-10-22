package chess;

import java.util.Collection;

public class MoveUtils {
    public static void addValidMoves(Collection<ChessMove> moves, ChessBoard board, ChessPosition startPosition, Collection<ChessPosition> possiblePositions) {
        ChessPiece startPiece = board.getPiece(startPosition);

        for (ChessPosition endPosition : possiblePositions) {
            ChessPiece endPiece = board.getPiece(endPosition);

            if (endPiece == null) {
                moves.add(new ChessMove(startPosition, endPosition, null));
            } else if (endPiece.getTeamColor() != startPiece.getTeamColor()) {
                moves.add(new ChessMove(startPosition, endPosition, null));
            }
        }
    }
}
