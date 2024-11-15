package ui;

import java.util.InputMismatchException;
import java.util.Scanner;

import exception.ServiceException;

public class PostLoginRepl {
    private final PostLoginClient postLoginClient; // Dependency on the client

    public PostLoginRepl(PostLoginClient postLoginClient) {
        this.postLoginClient = postLoginClient;
    }

    public boolean run() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Logged in! Type 'help' to get started.");

        boolean running = true;
        while (running) {
            System.out.print("[PostLogin] >>> ");
            String input = scanner.nextLine().trim().toLowerCase();

            switch (input) {
                case "help" -> displayHelp();
                case "logout" -> {
                    handleLogout(scanner);
                    return true;
                }
                case "create" -> handleCreate(scanner);
                case "join" -> {
                    running = false;
                    handleJoin(scanner);
                }
                case "observe" -> {
                    running = false;
                    handleObserve(scanner);
                }
                case "list" -> handleList(scanner);
                default -> System.out.println("Unknown command. Type 'help' for a list of valid commands.");
            }
        }
        return false;
    }

    private void displayHelp() {
        System.out.println("""
                Commands:
                - create: Make a new game.
                - list: See all current games.
                - join: Join a chess game.
                - observe: Observe a chess game.
                - logout: Logout of your session.
                - help: Display this help text.
                """);
    }

    private void handleCreate(Scanner scanner) {
        try {
            System.out.print("Enter game name: ");
            String name = scanner.nextLine().trim();

            postLoginClient.create(name);
            System.out.println("Created game!");
        } catch (ServiceException ex) {
            System.out.println("Creation failed: " + ex.getMessage());
        }
    }

    private void handleList(Scanner scanner) {
        try {
            String games = postLoginClient.list();
            if (games.isEmpty()) {
                System.out.println("No games found!");
            } else {
                System.out.println(games);
            }
        } catch (ServiceException ex) {
            System.out.println("Listing failed: " + ex.getMessage());
        }
    }

    private void handleLogout(Scanner scanner) {
        try {
            postLoginClient.logout();
        } catch (ServiceException ex) {
            System.out.println("Logout failed: " + ex.getMessage());
        }
    }

    private void handleObserve(Scanner scanner) {
        System.out.print("Enter game # to observe: ");
        int gameNumber;
        try {
            gameNumber = scanner.nextInt();
            scanner.nextLine(); // Consume the newline character after the integer

            //use id
            postLoginClient.observe(gameNumber);

        } catch (ServiceException ex) {
            System.out.println("Observe failed: " + ex.getMessage());
        }

    }

    private void handleJoin(Scanner scanner) {
        try {
            System.out.print("Enter game number to join: ");
            int gameNumber = scanner.nextInt();
            scanner.nextLine();

            System.out.print("Enter color ('WHITE' or 'BLACK'): ");
            String color = scanner.nextLine().trim().toUpperCase();

            postLoginClient.join(gameNumber, color);

            System.out.println("Joined game successfully!");

        } catch (ServiceException ex) {
            System.out.println("Join failed: " + ex.getMessage());
        } catch (InputMismatchException e) {
            System.out.println("Invalid input. Please enter a valid game number.");
            scanner.nextLine();
        }
    }
}

