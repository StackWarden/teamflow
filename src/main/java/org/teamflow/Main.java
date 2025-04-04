package org.teamflow;

import org.teamflow.controllers.ProjectController;
import org.teamflow.controllers.UserController;
import org.teamflow.database.DatabaseConnection;
import org.teamflow.models.Project;
import org.teamflow.models.ProjectCreationResult;
import org.teamflow.models.User;
import org.teamflow.services.UserProjectRoleService;

import java.util.Scanner;

public class Main {
    public static UserController userController = new UserController();
    public static ProjectController projectController = new ProjectController();
    private static final Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        DatabaseConnection.checkConnection();

        while (!userController.isLoggedIn()) {
            int choice = getLoginOrRegister();
            if (choice == 1) {
                login();
            } else {
                register();
            }
        }
        System.out.println("Welcome! You are now logged in.");
        createProject();
    }

    public static int getLoginOrRegister() {
        while (true) {
            System.out.println("Do you want to login or sign up?");
            System.out.println("1. Login");
            System.out.println("2. Sign up");

            try {
                int input = Integer.parseInt(scanner.nextLine());

                if (input == 1 || input == 2) {
                    System.out.println("You chose: " + input);
                    return input;
                } else {
                    System.out.println("Input 1 or 2");
                }
            } catch (NumberFormatException e) {
                System.out.println("Input 1 or 2");
            }
        }
    }

    public static void login() {
        System.out.println("What is your name?");
        String name = scanner.nextLine();

        int status = userController.loginUser(name);
        if (status == 2) {
            System.out.println("User not found.");
        }
    }

    public static void register() {
        System.out.println("What is your name?");
        String name = scanner.nextLine();

        int registerStatus = userController.registerUser(name);

        switch (registerStatus) {
            case 1 -> System.out.println("You have successfully registered!");
            case 2 -> {
                System.out.println("This user already exists, try another one!");
                register();
            }
        }
    }

    public static void createProject() {
        System.out.println("What is your project name?");
        String name = scanner.nextLine();
        System.out.println("What is your description?");
        String description = scanner.nextLine();

        ProjectCreationResult result = projectController.createProject(name, description);
        Project project = result.getProject();


        if (result.getStatus() == 1) {
            System.out.println("Project successfully created!");
        }
        else if(result.getStatus() == 2) {
            System.out.println("Project already exists, try another one!");
            return;
        }
        UserProjectRoleService.assignRoleToUser(userController.getUserId(), project.getId(), "Scrum Master");
    }
}
