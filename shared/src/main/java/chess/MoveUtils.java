package chess;

import java.util.Collection;

public class MoveUtils {
    public static void addValidMoves(Collection<ChessMove> moves, ChessBoard board, ChessPosition sPosition, Collection<ChessPosition> positions) {
        ChessPiece startPiece = board.getPiece(sPosition);

        for (ChessPosition endPosition : positions) {
            ChessPiece endPiece = board.getPiece(endPosition);

            if (endPiece == null) {
                moves.add(new ChessMove(sPosition, endPosition, null));
            } else if (endPiece.getTeamColor() != startPiece.getTeamColor()) {
                moves.add(new ChessMove(sPosition, endPosition, null));
            }
        }
    }
}
