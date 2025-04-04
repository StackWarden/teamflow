package org.teamflow.screens;

import org.teamflow.ScreenManager;
import org.teamflow.controllers.ProjectController;
import org.teamflow.controllers.UserController;
import org.teamflow.interfaces.Screen;
import java.util.Scanner;

public class ProjectScreen implements Screen {

    protected final Scanner scanner;
    protected final ProjectController projectController;
    protected final UserController userController;
    protected final ScreenManager screenManager;

    public ProjectScreen(Scanner scanner, ProjectController projectController, UserController userController, ScreenManager screenManager) {
        this.scanner = scanner;
        this.projectController = projectController;
        this.userController = userController;
        this.screenManager = screenManager;
    }

    @Override
    public void show() {
        boolean running = true;

        while (running) {
            printMenu();

            String input = scanner.nextLine();

            switch (input) {
                case "1" -> handleOption1();
                case "2" -> handleOption2();
                case "0" -> {
                    System.out.println("Returning to previous menu...");
                    running = false;
                }
                default -> System.out.println("Invalid input. Please try again.");
            }
        }
    }

    protected void printMenu() {
        System.out.println("===== " + projectController.getProjectNameAndUserRole(userController.getLoggedUser()) + " =====");
        System.out.println("1. Option 1");
        System.out.println("2. Option 2");
        System.out.println("0. Back");
    }

    protected void handleOption1() {
        System.out.println("Handling option 1...");
    }

    protected void handleOption2() {
        System.out.println("Handling option 2...");
    }
}
