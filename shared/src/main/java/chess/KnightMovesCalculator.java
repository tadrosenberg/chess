package chess;

import java.util.Collection;
import java.util.ArrayList;

public class KnightMovesCalculator implements PieceMovesCalculator {

    @Override
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition position) {
        Collection<ChessMove> moves = new ArrayList<>();
        // Implement logic to calculate knight's moves
        for (ChessPosition pos : getKnightMoves(board, position)) {
            if (board.getPiece(pos) == null) {
                moves.add(new ChessMove(position, pos, null));
            } else if (board.getPiece(pos).getTeamColor() != board.getPiece(position).getTeamColor()) {
                moves.add(new ChessMove(position, pos, null));
            }
        }
        return moves;
    }

    private Collection<ChessPosition> getKnightMoves(ChessBoard board, ChessPosition myPosition) {
        ArrayList<ChessPosition> knightMoves = new ArrayList<>();
        int currentRow = myPosition.getRow();
        int currentCol = myPosition.getColumn();
        int[][] directions = {
                {2, 1},
                {2, -1},
                {1, 2},
                {-1, 2},
                {1, -2},
                {-1, -2},
                {-2, 1},
                {-2, -1}
        };

        for (int[] direction : directions) {
            int newRow = currentRow + direction[0];
            int newCol = currentCol + direction[1];

            // Check if the new position is within bounds
            if (isWithinBounds(newRow, newCol)) {
                ChessPosition newPosition = new ChessPosition(newRow, newCol);

                // Check if the new position is blocked
                ChessPiece pieceAtNewPosition = board.getPiece(newPosition);
                if (pieceAtNewPosition == null) {
                    // If no piece is at the new position, it's a valid move
                    knightMoves.add(newPosition);
                } else if (pieceAtNewPosition.getTeamColor() != board.getPiece(myPosition).getTeamColor()) {
                    // If the piece is of the opposite color, it's a valid capture move
                    knightMoves.add(newPosition);
                }
                // No need for a break since the knight only moves one square
            }
        }

        return knightMoves;
    }

    private boolean isWithinBounds(int row, int col) {
        return row >= 1 && row < 9 && col >= 1 && col < 9; // Assuming an 8x8 chessboard
    }
}