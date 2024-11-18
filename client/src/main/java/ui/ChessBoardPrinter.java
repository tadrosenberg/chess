package ui;

import chess.ChessBoard;
import chess.ChessGame;
import chess.ChessPiece;
import chess.ChessPosition;

public class ChessBoardPrinter {

    public static void printBoard(ChessBoard board) {
        System.out.println("Black's Perspective:");
        renderBoard(board, false);
        System.out.println("\nWhite's Perspective:");
        renderBoard(board, true);
    }

    private static void renderBoard(ChessBoard board, boolean isWhitePerspective) {
        String columns = isWhitePerspective ? "   a  b  c  d  e  f  g  h" : "   h  g  f  e  d  c  b  a";
        System.out.println(columns);

        int startRow = isWhitePerspective ? 8 : 1;
        int endRow = isWhitePerspective ? 1 : 8;
        int rowStep = isWhitePerspective ? -1 : 1;

        for (int row = startRow; row != endRow + rowStep; row += rowStep) {
            System.out.print(row + " ");

            for (int col = 1; col <= 8; col++) {
                int displayCol = isWhitePerspective ? 9 - col : col;
                ChessPosition pos = new ChessPosition(row, displayCol);
                ChessPiece piece = board.getPiece(pos);

                boolean isLightSquare = (row + displayCol) % 2 == 0;
                String squareColor = isLightSquare
                        ? EscapeSequences.SET_BG_COLOR_LIGHT_GREY
                        : EscapeSequences.SET_BG_COLOR_DARK_GREY;

                String pieceDisplay = piece != null
                        ? getUnicodeForPiece(piece)
                        : EscapeSequences.EMPTY;

                System.out.print(squareColor + pieceDisplay + EscapeSequences.RESET_BG_COLOR);
            }

            System.out.println(" " + row);
        }

        System.out.println(columns);
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
