package ui;

import java.util.Scanner;

import exception.ServiceException;
import result.LoginResult;

public class PreLoginRepl {
    private final PreLoginClient preLoginClient; // Dependency on the client

    public PreLoginRepl(PreLoginClient preLoginClient) {
        this.preLoginClient = preLoginClient;
    }

    public void run() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Welcome to Chess! Type 'help' to get started.");

        boolean running = true;
        while (running) {
            System.out.print("[PreLogin] >>> ");
            String input = scanner.nextLine().trim().toLowerCase();

            switch (input) {
                case "help" -> displayHelp();
                case "register" -> handleRegister(scanner);
                case "login" -> {
                    LoginResult loginResult = handleLogin(scanner);
                    if (loginResult != null) {
                        System.out.println("Login successful! Transitioning to PostLogin...");
                        running = false;
                    }
                }
                case "quit" -> {
                    System.out.println("Goodbye!");
                    running = false;
                }
                default -> System.out.println("Unknown command. Type 'help' for a list of valid commands.");
            }
        }
    }

    private void displayHelp() {
        System.out.println("""
                Commands:
                - register: Create a new account.
                - login: Log in to an existing account.
                - quit: Exit the application.
                - help: Display this help text.
                """);
    }

    private void handleRegister(Scanner scanner) {
        try {
            System.out.print("Enter username: ");
            String username = scanner.nextLine().trim();
            System.out.print("Enter password: ");
            String password = scanner.nextLine().trim();
            System.out.print("Enter email: ");
            String email = scanner.nextLine().trim();

            preLoginClient.register(username, password, email);
            System.out.println("Registration successful! Call the login command next.");
        } catch (ServiceException ex) {
            System.out.println("Registration failed: " + ex.getMessage());
        }
    }

    private LoginResult handleLogin(Scanner scanner) {
        try {
            System.out.print("Enter username: ");
            String username = scanner.nextLine().trim();
            System.out.print("Enter password: ");
            String password = scanner.nextLine().trim();

            return preLoginClient.login(username, password);
        } catch (ServiceException ex) {
            System.out.println("Login failed: " + ex.getMessage());
            return null;
        }
    }


}

