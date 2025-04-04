package org.teamflow.screens;

import org.teamflow.ScreenManager;
import org.teamflow.controllers.ProjectController;
import org.teamflow.controllers.UserController;
import org.teamflow.interfaces.Screen;
import org.teamflow.services.UserProjectRoleService;

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
                case "9" -> {
                    if(deleteProject()) {
                        running = false;
                    }
                }
                case "0" -> {
                    System.out.println("Returning to previous menu...");
                    running = false;
                }
                default -> System.out.println("Invalid input. Please try again.");
            }
        }
    }

    protected void printMenu() {
        boolean isScrumMaster = UserProjectRoleService.isScrumMaster(userController.getUserId(), projectController.getCurrentProjectId());

        System.out.println("===== " + projectController.getProjectNameAndUserRole(userController.getLoggedUser()) + " =====");
        System.out.println("1. Option 1");
        System.out.println("2. Option 2");
        if ( isScrumMaster ) {
            System.out.println("9. Delete Project");
        }
        System.out.println("0. Back");
    }

    protected boolean deleteProject() {
        boolean isScrumMaster = UserProjectRoleService.isScrumMaster(userController.getUserId(), projectController.getCurrentProjectId());

        if (!isScrumMaster) {
            return false;
        }

        String projectInfo = projectController.getProjectNameAndUserRole(userController.getLoggedUser());

        System.out.println("You are about to permanently delete the project:");
        System.out.println("- " + projectInfo);
        System.out.println();
        System.out.println("This action cannot be undone.");
        System.out.print("To confirm, please type the exact project name: ");

        String choice = scanner.nextLine();

        if (choice.equals(projectController.getCurrentProjectName())) {
            projectController.deleteProject();
            System.out.println("Project has been successfully deleted.");
            return true;
        }

        System.out.println("Project name did not match. Deletion cancelled.");
        return false;
    }

    protected void handleOption1() {
        System.out.println("Handling option 1...");
    }

    protected void handleOption2() {
        System.out.println("Handling option 2...");
    }
}
