package ui;

import java.util.Scanner;

import exception.ServiceException;
import model.UserData;
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

}

