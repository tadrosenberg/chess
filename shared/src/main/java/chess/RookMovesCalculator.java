package chess;

import java.util.Collection;
import java.util.ArrayList;

public class RookMovesCalculator implements PieceMovesCalculator {

    @Override
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition position) {
        Collection<ChessMove> moves = new ArrayList<>();
        Collection<ChessPosition> possiblePositions = getRookMoves(board, position);
        MoveUtils.addValidMoves(moves, board, position, possiblePositions);
        return moves;
    }

    private Collection<ChessPosition> getRookMoves(ChessBoard board, ChessPosition myPosition) {
        ArrayList<ChessPosition> rookMoves = new ArrayList<>();
        int currentRow = myPosition.getRow();
        int currentCol = myPosition.getColumn();
        int[][] directions = {
                {1, 0},
                {0, 1},
                {-1, 0},
                {0, -1}
        };

        // Iterate over each direction
        for (int[] direction : directions) {
            int rowOffset = direction[0];
            int colOffset = direction[1];
            int newRow = currentRow + rowOffset;
            int newCol = currentCol + colOffset;

            // Continue moving in the current direction until the move is invalid
            while (isWithinBounds(newRow, newCol)) {
                ChessPosition newPosition = new ChessPosition(newRow, newCol);

                // Check if the new position is blocked
                ChessPiece pieceAtNewPosition = board.getPiece(newPosition);
                if (pieceAtNewPosition == null) {
                    // If no piece is at the new position, it's a valid move
                    rookMoves.add(newPosition);
                } else {
                    // If there is a piece, check if it can be captured
                    if (pieceAtNewPosition.getTeamColor() != board.getPiece(myPosition).getTeamColor()) {
                        // Can capture the piece
                        rookMoves.add(newPosition);
                    }
                    // Stop in this direction after encountering any piece
                    break;
                }

                // Move further in the same direction
                newRow += rowOffset;
                newCol += colOffset;
            }
        }

        return rookMoves;
    }

    private boolean isWithinBounds(int row, int col) {
        return row >= 1 && row < 9 && col >= 1 && col < 9; // Assuming an 8x8 chessboard
    }
}