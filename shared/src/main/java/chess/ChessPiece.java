package chess;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Represents a single chess piece
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessPiece {

    private final ChessGame.TeamColor pieceColor;
    private final PieceType type;

    public ChessPiece(ChessGame.TeamColor pieceColor, ChessPiece.PieceType type) {
        this.pieceColor = pieceColor;
        this.type = type;
    }

    /**
     * The various different chess piece options
     */
    public enum PieceType {
        KING,
        QUEEN,
        BISHOP,
        KNIGHT,
        ROOK,
        PAWN
    }

    /**
     * @return Which team this chess piece belongs to
     */
    public ChessGame.TeamColor getTeamColor() {
        return pieceColor;
    }

    /**
     * @return which type of chess piece this piece is
     */
    public PieceType getPieceType() {
        return type;
    }

    /**
     * Calculates all the positions a chess piece can move to
     * Does not take into account moves that are illegal due to leaving the king in
     * danger
     *
     * @return Collection of valid moves
     */
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        Collection<ChessMove> moves = new ArrayList<>();

        if (type == ChessPiece.PieceType.KING) {
            for (ChessPosition position : getKingMoves(myPosition)) {
                moves.add(new ChessMove(myPosition, position, board.getPiece(position).getPieceType()));
            }
        }
        if (type == ChessPiece.PieceType.QUEEN) {
            for (ChessPosition position : getQueenMoves(myPosition)) {
                moves.add(new ChessMove(myPosition, position, board.getPiece(position).getPieceType()));
            }
        }
        if (type == ChessPiece.PieceType.BISHOP) {
            for (ChessPosition position : getBishopMoves(myPosition)) {
                moves.add(new ChessMove(myPosition, position, board.getPiece(position).getPieceType()));
            }
        }
        if (type == ChessPiece.PieceType.KNIGHT) {
            for (ChessPosition position : getKnightMoves(myPosition)) {
                moves.add(new ChessMove(myPosition, position, board.getPiece(position).getPieceType()));
            }
        }
        if (type == ChessPiece.PieceType.ROOK) {
            for (ChessPosition position : getRookMoves(myPosition)) {
                moves.add(new ChessMove(myPosition, position, board.getPiece(position).getPieceType()));
            }
        }
        if (type == ChessPiece.PieceType.PAWN) {
            for (ChessPosition position : getPawnMoves(myPosition)) {
                moves.add(new ChessMove(myPosition, position, board.getPiece(position).getPieceType()));
            }
        }

        return moves;
    }

    public Collection<ChessPosition> getKingMoves(ChessPosition myPosition) {
        ArrayList<ChessPosition> kingMoves = new ArrayList<>();
        return kingMoves;
    }

    public Collection<ChessPosition> getQueenMoves(ChessPosition myPosition) {
        ArrayList<ChessPosition> queenMoves = new ArrayList<>();
        return queenMoves;
    }

    public Collection<ChessPosition> getBishopMoves(ChessPosition myPosition) {
        ArrayList<ChessPosition> bishopMoves = new ArrayList<>();
        return bishopMoves;
    }

    public Collection<ChessPosition> getRookMoves(ChessPosition myPosition) {
        ArrayList<ChessPosition> rookMoves = new ArrayList<>();
        return rookMoves;
    }

    public Collection<ChessPosition> getKnightMoves(ChessPosition myPosition) {
        ArrayList<ChessPosition> knightMoves = new ArrayList<>();
        return knightMoves;
    }

    public Collection<ChessPosition> getPawnMoves(ChessPosition myPosition) {
        ArrayList<ChessPosition> pawnMoves = new ArrayList<>();
        return pawnMoves;
    }
}
