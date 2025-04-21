package org.teamflow.screens;

import org.teamflow.ScreenManager;
import org.teamflow.enums.ScreenType;
import org.teamflow.abstracts.Screen;

import static org.teamflow.ScreenManager.clearScreen;

public class LoginScreen extends Screen {

    public LoginScreen(ScreenManager screenManager) {
        super(screenManager);
    }

    @Override
    public void show() {
        while (true) {
            clearScreen();
            printBreadcrumb("Login/Register");

            if (userController.isLoggedIn()) {
                screenManager.switchTo(ScreenType.DASHBOARD);
                return;
            }

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
                        setAlertMessage("Welcome back, " + name + "!");
                    } else {
                        setAlertMessage("User not found.");
                    }
                }
                case "2" -> {
                    System.out.print("Username: ");
                    String name = scanner.nextLine();
                    int status = userController.registerUser(name);
                    if (status == 1) {
                        setAlertMessage("User successfully registered.");
                    } else {
                        setAlertMessage("Username already exists.");
                    }
                }
                case "3" -> {
                    System.out.println("Exiting application...");
                    System.exit(0);
                }
                default -> setAlertMessage("Invalid input.");
            }
        }
    }
}
