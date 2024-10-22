package chess;

import java.util.Arrays;
import java.util.Objects;

/**
 * A chessboard that can hold and rearrange chess pieces.
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessBoard {
    private ChessPiece[][] squares = new ChessPiece[8][8];

    public ChessBoard() {

    }

    /**
     * Adds a chess piece to the chessboard
     *
     * @param position where to add the piece to
     * @param piece    the piece to add
     */
    public void addPiece(ChessPosition position, ChessPiece piece) {
        squares[position.getRow() - 1][position.getColumn() - 1] = piece;
    }

    /**
     * Gets a chess piece on the chessboard
     *
     * @param position The position to get the piece from
     * @return Either the piece at the position, or null if no piece is at that
     * position
     */
    public ChessPiece getPiece(ChessPosition position) {
        return squares[position.getRow() - 1][position.getColumn() - 1];
    }

    /**
     * Sets the board to the default starting board
     * (How the game of chess normally starts)
     */
    public void resetBoard() {
        clear();
        setUpPieceRow(1, ChessGame.TeamColor.WHITE);
        setUpPawns(2, ChessGame.TeamColor.WHITE);
        setUpPieceRow(8, ChessGame.TeamColor.BLACK);
        setUpPawns(7, ChessGame.TeamColor.BLACK);

    }

    private void setUpPawns(int row, ChessGame.TeamColor color) {
        for (int col = 1; col <= 8; col++) {
            addPiece(new ChessPosition(row, col), new ChessPiece(color, ChessPiece.PieceType.PAWN));
        }
    }

    private void setUpPieceRow(int row, ChessGame.TeamColor color) {
        addPiece(new ChessPosition(row, 1), new ChessPiece(color, ChessPiece.PieceType.ROOK));
        addPiece(new ChessPosition(row, 2), new ChessPiece(color, ChessPiece.PieceType.KNIGHT));
        addPiece(new ChessPosition(row, 3), new ChessPiece(color, ChessPiece.PieceType.BISHOP));
        addPiece(new ChessPosition(row, 4), new ChessPiece(color, ChessPiece.PieceType.QUEEN));
        addPiece(new ChessPosition(row, 5), new ChessPiece(color, ChessPiece.PieceType.KING));
        addPiece(new ChessPosition(row, 6), new ChessPiece(color, ChessPiece.PieceType.BISHOP));
        addPiece(new ChessPosition(row, 7), new ChessPiece(color, ChessPiece.PieceType.KNIGHT));
        addPiece(new ChessPosition(row, 8), new ChessPiece(color, ChessPiece.PieceType.ROOK));
    }

    public void clear() {
        for (ChessPiece[] square : squares) {
            Arrays.fill(square, null);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessBoard that = (ChessBoard) o;
        return Objects.deepEquals(squares, that.squares);
    }

    @Override
    public int hashCode() {
        return Arrays.deepHashCode(squares);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("ChessBoard{\n");

        // Iterate through each row
        for (int row = squares.length - 1; row >= 0; row--) { // Start from top to bottom
            sb.append("|");
            for (int col = 0; col < squares[row].length; col++) {
                ChessPiece piece = squares[row][col];
                if (piece == null) {
                    sb.append(" "); // Empty square
                } else {
                    sb.append(getPieceSymbol(piece));
                }
                sb.append("|");
            }
            sb.append("\n");
        }
        sb.append("}");
        return sb.toString();
    }

    /**
     * Helper method to get the symbol of a piece
     * Lowercase for black pieces, uppercase for white pieces
     */
    private char getPieceSymbol(ChessPiece piece) {
        ChessPiece.PieceType type = piece.getPieceType();
        ChessGame.TeamColor color = piece.getTeamColor();

        char symbol;
        switch (type) {
            case KING -> symbol = 'K';
            case QUEEN -> symbol = 'Q';
            case ROOK -> symbol = 'R';
            case BISHOP -> symbol = 'B';
            case KNIGHT -> symbol = 'N';
            case PAWN -> symbol = 'P';
            default -> throw new IllegalArgumentException("Unknown piece type: " + type);
        }

        // Return lowercase for black pieces
        return (color == ChessGame.TeamColor.BLACK) ? Character.toLowerCase(symbol) : symbol;
    }

}
