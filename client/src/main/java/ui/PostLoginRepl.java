package ui;

import java.util.Scanner;

import exception.ServiceException;
import result.LoginResult;

public class PostLoginRepl {
    private final PostLoginClient postLoginClient; // Dependency on the client

    public PostLoginRepl(PostLoginClient postLoginClient) {
        this.postLoginClient = postLoginClient;
    }

    public void run() {
        Scanner scanner = new Scanner(System.in);

        boolean running = true;
        while (running) {
            System.out.print("[PostLogin] >>> ");
            String input = scanner.nextLine().trim().toLowerCase();

            switch (input) {
                case "help" -> displayHelp();
                case "logout" -> handleLogout(scanner);
                case "create" -> handleCreate(scanner);
                case "join" -> handleJoin(scanner);
                case "observe" -> handleObserve(scanner);
                case "list" -> handleList(scanner);
                default -> System.out.println("Unknown command. Type 'help' for a list of valid commands.");
            }
        }
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
            postLoginClient.list();
        } catch (ServiceException ex) {
            System.out.println("Listing failed: " + ex.getMessage());
        }
    }
}

