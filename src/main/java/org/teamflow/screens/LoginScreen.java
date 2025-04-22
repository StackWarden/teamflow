package org.teamflow.screens;

import org.teamflow.ScreenManager;
import org.teamflow.enums.ScreenType;
import org.teamflow.abstracts.Screen;

import java.util.ArrayList;
import java.util.List;

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

            List<MenuOption> options = new ArrayList<>();
            options.add(new MenuOption("Login", this::handleLogin));
            options.add(new MenuOption("Register", this::handleRegister));
            options.add(new MenuOption("Exit", () -> {
                System.out.println("Exiting application...");
                System.exit(0);
            }));

            displayMenu(options, null);
        }
    }

    private void handleLogin() {
        System.out.print("Username: ");
        String name = scanner.nextLine();
        int status = userController.loginUser(name);
        if (status == 1) {
            setAlertMessage("Welcome back, " + name + "!");
        } else {
            setAlertMessage("User not found.");
        }
    }

    private void handleRegister() {
        System.out.print("Username: ");
        String name = scanner.nextLine();
        int status = userController.registerUser(name);
        if (status == 1) {
            setAlertMessage("User successfully registered.");
        } else {
            setAlertMessage("Username already exists.");
        }
    }
}
