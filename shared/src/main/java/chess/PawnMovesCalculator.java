package chess;

import java.util.Collection;
import java.util.ArrayList;

import static chess.ChessPiece.PieceType.*;
import static chess.ChessGame.TeamColor.*;

public class PawnMovesCalculator {

    public static Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition position) {
        Collection<ChessMove> moves = new ArrayList<>();
        // Implement logic to calculate pawn's moves
        for (ChessPosition pos : getPawnMoves(board, position)) {

            boolean isPromotionMove = (board.getPiece(position).getTeamColor() == WHITE && pos.getRow() == 8) ||
                    (board.getPiece(position).getTeamColor() == BLACK && pos.getRow() == 1);

            if (board.getPiece(pos) == null) {
                doPromotionMove(position, moves, pos, isPromotionMove);
            } else if (board.getPiece(pos).getTeamColor() != board.getPiece(position).getTeamColor()) {
                doPromotionMove(position, moves, pos, isPromotionMove);
            }
        }
        return moves;
    }

    private static void doPromotionMove(ChessPosition position, Collection<ChessMove> moves, ChessPosition pos, boolean isPromotionMove) {
        if (isPromotionMove) {
            moves.add(new ChessMove(position, pos, QUEEN));
            moves.add(new ChessMove(position, pos, ROOK));
            moves.add(new ChessMove(position, pos, KNIGHT));
            moves.add(new ChessMove(position, pos, BISHOP));
        } else {
            moves.add(new ChessMove(position, pos, null));
        }
    }

    private static Collection<ChessPosition> getPawnMoves(ChessBoard board, ChessPosition myPosition) {
        ArrayList<ChessPosition> pawnMoves = new ArrayList<>();
        int currentRow = myPosition.getRow();
        int currentCol = myPosition.getColumn();
        ChessPiece piece = board.getPiece(myPosition);
        ChessGame.TeamColor color = piece.getTeamColor();

        // Determine the direction of movement based on color
        int direction = (color == ChessGame.TeamColor.WHITE) ? 1 : -1;

        // Standard one-square move forward
        int newRow = currentRow + direction;
        if (isWithinBounds(newRow, currentCol) && board.getPiece(new ChessPosition(newRow, currentCol)) == null) {
            pawnMoves.add(new ChessPosition(newRow, currentCol));

            // Initial two-square move forward
            if ((color == ChessGame.TeamColor.WHITE && currentRow == 2) || (color == ChessGame.TeamColor.BLACK && currentRow == 7)) {
                int twoSquaresForward = currentRow + 2 * direction;
                if (isWithinBounds(twoSquaresForward, currentCol) && board.getPiece(new ChessPosition(twoSquaresForward, currentCol)) == null) {
                    pawnMoves.add(new ChessPosition(twoSquaresForward, currentCol));
                }
            }
        }

        // Diagonal capture moves
        int[][] captureDirections = {{direction, 1}, {direction, -1}};
        for (int[] offset : captureDirections) {
            int captureRow = currentRow + offset[0];
            int captureCol = currentCol + offset[1];
            if (isWithinBounds(captureRow, captureCol)) {
                ChessPosition capturePosition = new ChessPosition(captureRow, captureCol);
                ChessPiece pieceAtCapture = board.getPiece(capturePosition);
                if (pieceAtCapture != null && pieceAtCapture.getTeamColor() != color) {
                    pawnMoves.add(capturePosition);
                }
            }
        }

        return pawnMoves;
    }

    private static boolean isWithinBounds(int row, int col) {
        return row >= 1 && row < 9 && col >= 1 && col < 9; // Assuming an 8x8 chessboard
    }
}
