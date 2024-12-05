package ui;

import chess.ChessGame;
import chess.ChessMove;
import chess.ChessPosition;
import exception.ServiceException;
import model.GameData;
import websocket.messages.ErrorMessage;
import websocket.messages.LoadGameMessage;
import websocket.messages.NotificationMessage;
import websocket.messages.ServerMessage;

import java.util.Scanner;

public class GameplayRepl implements ServerMessageObserver {
    private final GameplayClient gameplayClient;
    private final String userColor;
    private ChessGame game;

    public GameplayRepl(String serverUrl, String authToken, int gameID, String playerColor, ChessGame game) throws ServiceException {
        WebSocketFacade webSocketFacade = new WebSocketFacade(serverUrl, this); // Pass REPL as observer
        this.gameplayClient = new GameplayClient(webSocketFacade, gameID, authToken);
        this.game = game;
        this.userColor = playerColor;
    }


    public void run() {
        Scanner scanner = new Scanner(System.in);

        try {
            gameplayClient.connect();
            System.out.println("Connected to the game! Type 'help' for available commands.");

            boolean running = true;
            while (running) {
                System.out.print("[Gameplay] >>> ");
                String input = scanner.nextLine().trim().toLowerCase();

                switch (input) {
                    case "help" -> displayHelp();
                    case "leave" -> {
                        gameplayClient.leaveGame();
                        running = false;
                    }
                    case "resign" -> handleResign();
                    case "make move" -> handleMakeMove(scanner);
                    case "highlight moves" -> handleHighlightMoves(scanner);
                    case "redraw" -> redrawBoard();
                    default -> System.out.println("Unknown command. Type 'help' for available commands.");
                }
            }
        } catch (ServiceException ex) {
            System.out.println("Gameplay error: " + ex.getMessage());
        }
    }

    private void displayHelp() {
        System.out.println("""
                Commands:
                - help: Display this help text.
                - redraw: Redraw the chess board.
                - leave: Leave the game.
                - resign: Resign from the game.
                - make move: Make a move in the game.
                - highlight moves: Highlight legal moves for a specific piece.
                """);
    }

    private void handleResign() {
        try {
            System.out.print("Are you sure you want to resign? (yes/no): ");
            Scanner scanner = new Scanner(System.in);
            String input = scanner.nextLine().trim().toLowerCase();

            if ("yes".equals(input)) {
                gameplayClient.resignGame();
                System.out.println("You resigned from the game.");
            } else {
                System.out.println("Resignation canceled.");
            }
        } catch (ServiceException ex) {
            System.out.println("Resign failed: " + ex.getMessage());
        }
    }

    private void handleMakeMove(Scanner scanner) {
        try {
            System.out.print("Enter your move (e.g., e2 to e4): ");
            String input = scanner.nextLine().trim().toLowerCase();

            String[] positions = input.split(" to ");
            if (positions.length != 2) {
                throw new IllegalArgumentException("Invalid input format. Use 'e2 to e4'.");
            }

            ChessPosition startPos = parseChessPosition(positions[0]);
            ChessPosition endPos = parseChessPosition(positions[1]);
            ChessMove move = new ChessMove(startPos, endPos, null); // Handle promotions later

            gameplayClient.makeMove(move);
            System.out.println("Move made!");
        } catch (ServiceException ex) {
            System.out.println("Move failed: " + ex.getMessage());
        } catch (Exception ex) {
            System.out.println("Invalid input. Please enter positions in the format 'e2 to e4'.");
        }
    }

    private void handleHighlightMoves(Scanner scanner) {
        try {
            System.out.print("Enter piece position (e.g., 2,5): ");
            String[] position = scanner.nextLine().split(",");
            ChessPosition pos = new ChessPosition(Integer.parseInt(position[0]), Integer.parseInt(position[1]));

            // Call a method in GameplayClient to highlight moves (add functionality as needed)
            System.out.println("Highlighted legal moves for piece at " + pos);
        } catch (Exception ex) {
            System.out.println("Invalid input. Please enter positions in the format row,col.");
        }
    }

    private void redrawBoard() {
        System.out.println("Redrawing the board...");
        boolean isWhitePerspective = userColor == null || userColor.equals("WHITE");
        ChessBoardPrinter.printBoard(game.getBoard(), isWhitePerspective);
    }

    @Override
    public void notify(ServerMessage message) {
        switch (message) {
            case LoadGameMessage loadGameMessage -> {
                game = loadGameMessage.getGame();
                System.out.println("Game loaded! Drawing the board...");
                boolean isWhitePerspective = userColor == null || userColor.equals("WHITE");
                ChessBoardPrinter.printBoard(game.getBoard(), isWhitePerspective);
            }
            case NotificationMessage notification -> System.out.println(notification.getMessage());
            case ErrorMessage notification -> System.out.println(notification.getErrorMessage());
            case null, default -> System.out.println("Unknown message type received: " + message);
        }
    }

    private ChessPosition parseChessPosition(String input) {
        char colChar = input.charAt(0); // e.g., 'e'
        int row = Character.getNumericValue(input.charAt(1)); // e.g., '2'

        int col = colChar - 'a' + 1; // Convert 'a'-'h' to 1-8
        return new ChessPosition(row, col);
    }
}
