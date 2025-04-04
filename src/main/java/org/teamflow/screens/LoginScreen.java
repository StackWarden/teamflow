package org.teamflow.screens;

import org.teamflow.ScreenManager;
import org.teamflow.controllers.UserController;
import org.teamflow.enums.ScreenType;
import org.teamflow.interfaces.Screen;
import java.util.Scanner;

public class LoginScreen implements Screen {

    private final Scanner scanner;
    private final UserController userController;
    private final ScreenManager screenManager;

    public LoginScreen(Scanner scanner, UserController userController, ScreenManager screenManager) {
        this.scanner = scanner;
        this.userController = userController;
        this.screenManager = screenManager;
    }

    @Override
    public void show() {
        while (true) {
            if (userController.isLoggedIn()) {
                screenManager.switchTo(ScreenType.DASHBOARD);
                return;
            }

            System.out.println("===== Login Or Register =====");
            System.out.println("1. Login");
            System.out.println("2. Register");
            System.out.println("3. Exit");

            String input = scanner.nextLine();

            switch (input) {
                case "1" -> {
                    System.out.print("Username: ");
                    String name = scanner.nextLine();
                    int status = userController.loginUser(name);
                    if (status == 1) {
                        System.out.println("Welcome back, " + name + "!");
                    } else {
                        System.out.println("User not found.");
                    }
                }
                case "2" -> {
                    System.out.print("Username: ");
                    String name = scanner.nextLine();
                    int status = userController.registerUser(name);
                    if (status == 1) {
                        System.out.println("You are now registered, " + name + "!");
                    } else {
                        System.out.println("Username already exists.");
                    }
                }
                case "3" -> {
                    System.out.println("Exiting application...");
                    System.exit(0);
                }
                default -> System.out.println("Invalid choice. Try again.");
            }
        }
    }
}
