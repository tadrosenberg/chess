package ui;

import chess.ChessBoard;
import chess.ChessGame;
import chess.ChessPiece;
import chess.ChessPosition;

public class ChessBoardPrinter {

    /**
     * Prints the chess board from the specified team's perspective.
     *
     * @param board       the ChessBoard to print
     * @param perspective the perspective to print from (WHITE or BLACK)
     */
    public static void printBoard(ChessBoard board, ChessGame.TeamColor perspective) {
        System.out.println(perspective == ChessGame.TeamColor.WHITE ? "White's Perspective:" : "Black's Perspective:");
        renderBoard(board, perspective == ChessGame.TeamColor.WHITE);
    }

    private static void renderBoard(ChessBoard board, boolean isWhitePerspective) {
        // Set up row and column labels
        String columns = "  a b c d e f g h";
        if (!isWhitePerspective) {
            columns = new StringBuilder(columns).reverse().toString();
        }
        System.out.println(columns);

        // Iterate over rows and columns
        for (int row = isWhitePerspective ? 8 : 1;
             isWhitePerspective ? row >= 1 : row <= 8;
             row += isWhitePerspective ? -1 : 1) {
            System.out.print(row + " "); // Row label

            for (int col = isWhitePerspective ? 1 : 8;
                 isWhitePerspective ? col <= 8 : col >= 1;
                 col += isWhitePerspective ? 1 : -1) {

                ChessPosition pos = new ChessPosition(row, col);
                ChessPiece piece = board.getPiece(pos);

                // Choose square color
                boolean isLightSquare = (row + col) % 2 == 0;
                String squareColor = isLightSquare
                        ? EscapeSequences.SET_BG_COLOR_LIGHT_GREY
                        : EscapeSequences.SET_BG_COLOR_DARK_GREY;

                // Get piece Unicode or empty space
                String pieceDisplay = piece != null
                        ? getUnicodeForPiece(piece)
                        : EscapeSequences.EMPTY;

                System.out.print(squareColor + pieceDisplay + EscapeSequences.RESET_BG_COLOR);
            }

            System.out.println(" " + row); // Row label on the right
        }

        System.out.println(columns); // Columns at the bottom
    }

    private static String getUnicodeForPiece(ChessPiece piece) {
        return switch (piece.getPieceType()) {
            case KING -> piece.getTeamColor() == ChessGame.TeamColor.WHITE
                    ? EscapeSequences.WHITE_KING : EscapeSequences.BLACK_KING;
            case QUEEN -> piece.getTeamColor() == ChessGame.TeamColor.WHITE
                    ? EscapeSequences.WHITE_QUEEN : EscapeSequences.BLACK_QUEEN;
            case BISHOP -> piece.getTeamColor() == ChessGame.TeamColor.WHITE
                    ? EscapeSequences.WHITE_BISHOP : EscapeSequences.BLACK_BISHOP;
            case KNIGHT -> piece.getTeamColor() == ChessGame.TeamColor.WHITE
                    ? EscapeSequences.WHITE_KNIGHT : EscapeSequences.BLACK_KNIGHT;
            case ROOK -> piece.getTeamColor() == ChessGame.TeamColor.WHITE
                    ? EscapeSequences.WHITE_ROOK : EscapeSequences.BLACK_ROOK;
            case PAWN -> piece.getTeamColor() == ChessGame.TeamColor.WHITE
                    ? EscapeSequences.WHITE_PAWN : EscapeSequences.BLACK_PAWN;
        };
    }
}
