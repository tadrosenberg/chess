package chess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

/**
 * For a class that can manage a chess game, making moves on a board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessGame {
    private TeamColor teamTurn;
    private ChessBoard gameBoard;

    public ChessGame() {
        this.teamTurn = TeamColor.WHITE;
        this.gameBoard = new ChessBoard();
        gameBoard.resetBoard();
    }


    /**
     * @return Which team's turn it is
     */
    public TeamColor getTeamTurn() {
        return teamTurn;
    }

    /**
     * Set's which teams turn it is
     *
     * @param team the team whose turn it is
     */
    public void setTeamTurn(TeamColor team) {
        teamTurn = team;
    }

    /**
     * Enum identifying the 2 possible teams in a chess game
     */
    public enum TeamColor {
        WHITE,
        BLACK
    }

    /**
     * Gets a valid moves for a piece at the given location
     *
     * @param startPosition the piece to get valid moves for
     * @return Set of valid moves for requested piece, or null if no piece at
     * startPosition
     */
    public Collection<ChessMove> validMoves(ChessPosition startPosition) {
        Collection<ChessMove> moves = new ArrayList<>();

        ChessPiece currentPiece = gameBoard.getPiece(startPosition);
        if (currentPiece != null) {
            moves = currentPiece.pieceMoves(gameBoard, startPosition);
        }

        Iterator<ChessMove> iterator = moves.iterator();
        

        return moves;
    }

    /**
     * Makes a move in a chess game
     *
     * @param move chess move to preform
     * @throws InvalidMoveException if move is invalid
     */
    public void makeMove(ChessMove move) throws InvalidMoveException {
        ChessPiece pieceToMove = gameBoard.getPiece(move.getStartPosition());
        ChessPiece tempPiece = gameBoard.getPiece(move.getEndPosition());

        if (pieceToMove == null) {
            throw new InvalidMoveException("No piece there");
        }
        TeamColor color = pieceToMove.getTeamColor();
        if (color != teamTurn) {
            throw new InvalidMoveException("Wrong turn");
        }
        Collection<ChessMove> validMoves = validMoves(move.getStartPosition());
        if (!validMoves.contains(move)) {
            throw new InvalidMoveException("Not a valid move");
        }
        if (move.getPromotionPiece() != null) {
            gameBoard.addPiece(move.getStartPosition(), null);
            gameBoard.addPiece(move.getEndPosition(), new ChessPiece(color, move.getPromotionPiece()));
        } else {
            gameBoard.addPiece(move.getStartPosition(), null);
            gameBoard.addPiece(move.getEndPosition(), pieceToMove);
            if (isInCheck(pieceToMove.getTeamColor())) {
                gameBoard.addPiece(move.getEndPosition(), tempPiece);
                gameBoard.addPiece(move.getStartPosition(), pieceToMove);
                throw new InvalidMoveException();
            }
        }
        if (teamTurn == TeamColor.BLACK) {
            setTeamTurn(TeamColor.WHITE);
        } else {
            setTeamTurn(TeamColor.BLACK);
        }
    }

    /**
     * Determines if the given team is in check
     *
     * @param teamColor which team to check for check
     * @return True if the specified team is in check
     */
    public boolean isInCheck(TeamColor teamColor) {
        ChessPosition kingsLocation = findKingPosition(teamColor);

        for (int row = 1; row < 9; row++) {
            for (int col = 1; col < 9; col++) {
                ChessPosition currentPosition = new ChessPosition(row, col);
                ChessPiece currentPiece = gameBoard.getPiece(currentPosition);
                if (currentPiece != null && currentPiece.getTeamColor() != teamColor) {
                    Collection<ChessMove> currentMoves = currentPiece.pieceMoves(gameBoard, currentPosition);
                    for (ChessMove move : currentMoves) {
                        if (move.getEndPosition().equals(kingsLocation)) {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    public ChessPosition findKingPosition(TeamColor teamColor) {
        for (int row = 1; row < 9; row++) {
            for (int col = 1; col < 9; col++) {
                ChessPosition currentPosition = new ChessPosition(row, col);
                ChessPiece currentPiece = gameBoard.getPiece(currentPosition);
                if (currentPiece != null && currentPiece.getPieceType().equals(ChessPiece.PieceType.KING) && currentPiece.getTeamColor().equals(teamColor)) {
                    return currentPosition;
                }
            }
        }
        return null;
    }

    /**
     * Determines if the given team is in checkmate
     *
     * @param teamColor which team to check for checkmate
     * @return True if the specified team is in checkmate
     */
    public boolean isInCheckmate(TeamColor teamColor) {
        if (isInCheck(teamColor)) {
            for (int row = 1; row < 9; row++) {
                for (int col = 1; col < 9; col++) {
                    ChessPosition currentPosition = new ChessPosition(row, col);
                    ChessPiece currentPiece = gameBoard.getPiece(currentPosition);
                    if (currentPiece != null && currentPiece.getTeamColor() == teamColor) {
                        Collection<ChessMove> moves = validMoves(currentPosition);
                        for (ChessMove move : moves) {
                            ChessPiece tempPiece = gameBoard.getPiece(move.getEndPosition());
                            gameBoard.addPiece(move.getStartPosition(), null);
                            gameBoard.addPiece(move.getEndPosition(), currentPiece);
                            if (isInCheck(currentPiece.getTeamColor())) {
                                gameBoard.addPiece(move.getEndPosition(), tempPiece);
                                gameBoard.addPiece(move.getStartPosition(), currentPiece);
                            } else {
                                gameBoard.addPiece(move.getEndPosition(), tempPiece);
                                gameBoard.addPiece(move.getStartPosition(), currentPiece);
                                return false;
                            }
                        }
                    }
                }
            }
            return true;
        }
        return false;
    }

    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor) {
        if (isInCheck(teamColor)) {
            return false;
        }

        for (int row = 1; row < 9; row++) {
            for (int col = 1; col < 9; col++) {
                ChessPosition currentPosition = new ChessPosition(row, col);
                ChessPiece currentPiece = gameBoard.getPiece(currentPosition);
                if (currentPiece != null && currentPiece.getTeamColor().equals(teamColor)) {
                    Collection<ChessMove> moves = validMoves(currentPosition);
                    if (!moves.isEmpty()) {
                        return false;
                    }
                }
            }
        }

        return true;
    }

    /**
     * Sets this game's chessboard with a given board
     *
     * @param board the new board to use
     */
    public void setBoard(ChessBoard board) {
        gameBoard = board;
    }

    /**
     * Gets the current chessboard
     *
     * @return the chessboard
     */
    public ChessBoard getBoard() {
        return gameBoard;
    }
}
