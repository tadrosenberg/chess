package chess;

import java.util.Collection;
import java.util.ArrayList;

public class KingMovesCalculator implements PieceMovesCalculator {

    @Override
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition position) {
        Collection<ChessMove> moves = new ArrayList<>();
        Collection<ChessPosition> possiblePositions = getKingMoves(board, position);
        MoveUtils.addValidMoves(moves, board, position, possiblePositions);
        return moves;
    }

    private Collection<ChessPosition> getKingMoves(ChessBoard board, ChessPosition myPosition) {
        ArrayList<ChessPosition> kingMoves = new ArrayList<>();
        int currentRow = myPosition.getRow();
        int currentCol = myPosition.getColumn();
        int[][] directions = {
                {1, 0},
                {0, 1},
                {-1, 0},
                {0, -1},
                {1, 1},
                {1, -1},
                {-1, 1},
                {-1, -1}
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
                    kingMoves.add(newPosition);
                } else if (pieceAtNewPosition.getTeamColor() != board.getPiece(myPosition).getTeamColor()) {
                    // If the piece is of the opposite color, it's a valid capture move
                    kingMoves.add(newPosition);
                }
                // No need for a break since the King only moves one square
            }
        }

        return kingMoves;
    }

    private boolean isWithinBounds(int row, int col) {
        return row >= 1 && row < 9 && col >= 1 && col < 9; // Assuming an 8x8 chessboard
    }
}