package ui;

import chess.ChessMove;
import chess.ChessPosition;
import exception.ServiceException;
import websocket.messages.ServerMessage;

import java.util.Scanner;

public class GameplayRepl implements ServerMessageObserver {
    private final GameplayClient gameplayClient;

    public GameplayRepl(GameplayClient gameplayClient) {
        this.gameplayClient = gameplayClient;
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
                        System.out.println("You left the game.");
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
            System.out.print("Enter start position (e.g., 2,5): ");
            String[] start = scanner.nextLine().split(",");
            System.out.print("Enter end position (e.g., 4,5): ");
            String[] end = scanner.nextLine().split(",");

            ChessPosition startPos = new ChessPosition(Integer.parseInt(start[0]), Integer.parseInt(start[1]));
            ChessPosition endPos = new ChessPosition(Integer.parseInt(end[0]), Integer.parseInt(end[1]));
            ChessMove move = new ChessMove(startPos, endPos, null); // Handle promotions later

            gameplayClient.makeMove(move);
            System.out.println("Move made!");
        } catch (ServiceException ex) {
            System.out.println("Move failed: " + ex.getMessage());
        } catch (Exception ex) {
            System.out.println("Invalid input. Please enter positions in the format row,col.");
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
        // Redraw the board (fetch current game state and call ChessBoardPrinter)
        System.out.println("Redrawing the board...");
    }

    @Override
    public void notify(ServerMessage message) {
//        switch (serverMessage.getServerMessageType()) {
//            case NOTIFICATION -> System.out.println("Notification: " + serverMessage.getMessage());
//            case LOAD_GAME -> System.out.println("Game Loaded!");
//            case ERROR -> System.err.println("Error: " + serverMessage.getMessage());
//        }
    }
}
