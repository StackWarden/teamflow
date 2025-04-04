package org.teamflow.screens;

import org.teamflow.ScreenManager;
import org.teamflow.controllers.ProjectController;
import org.teamflow.database.DatabaseConnection;
import org.teamflow.enums.ScreenType;
import org.teamflow.interfaces.Screen;
import org.teamflow.controllers.UserController;
import org.teamflow.models.Project;
import org.teamflow.models.ProjectCreationResult;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;

public class DashboardScreen implements Screen {

    private final Scanner scanner;
    private final ProjectController projectController;
    private final UserController userController;
    private final ScreenManager screenManager;

    public DashboardScreen(Scanner scanner, ProjectController projectController, UserController userController, ScreenManager screenManager) {
        this.scanner = scanner;
        this.projectController = projectController;
        this.userController = userController;
        this.screenManager = screenManager;
    }

    @Override
    public void show() {
        while (true) {
            System.out.println("===== Dashboard =====");
            System.out.println("1. Create a new project");
            System.out.println("2. Join a project");
            System.out.println("3. View joined projects");
            System.out.println("4. Logout");
            System.out.println("5. Exit");
            System.out.println("9. Remove a user from a project");
            System.out.println("0. Delete account");

            String choice = scanner.nextLine();

            switch (choice) {
                case "0" -> {
                    userController.removeUserFromProject();
                    screenManager.switchTo(ScreenType.LOGIN);
                    return;
                }
                case "1" -> createProject();
                case "2" -> joinProject();
                case "3" -> System.out.println("TODO: list projects");
                case "4" -> {
                    userController.logout(); // You should add this method
                    screenManager.switchTo(ScreenType.LOGIN);
                    return;
                }
                case "5" -> {
                    System.out.println("Goodbye!");
                    System.exit(0);
                }
                case "9" -> {
                    projectController.removeUserFromProjectByName();
                }
                default -> System.out.println("Invalid option.");
            }
        }
    }

    private void createProject() {
        System.out.print("Enter project name: ");
        String name = scanner.nextLine();
        System.out.print("Enter description: ");
        String description = scanner.nextLine();

        ProjectCreationResult result = projectController.createProject(name, description);
        Project project = result.getProject();

        if (result.getStatus() == 1) {
            System.out.println("Project created!");
        } else if (result.getStatus() == 2) {
            System.out.println("Project already exists.");
        } else {
            System.out.println("Error creating project.");
        }
    }
    public void listProjects() {
        String sql = "SELECT name FROM project";
        try (
                PreparedStatement stmt = DatabaseConnection.getConnection().prepareStatement(sql);
                ResultSet rs = stmt.executeQuery()
        ) {
            int index = 1;
            while (rs.next()) {
                String name = rs.getString("name");
                System.out.println(index + ": " + name);
                index++;
            }

            if (index > index) {
                System.out.println("No projects found.");
            }
        } catch (SQLException e) {
            System.out.println("Failed to list projects: " + e.getMessage());
        }
    }

    private void joinProject() {
        System.out.println("Which project to join?");
        listProjects();
        String choice = scanner.nextLine();
        switch (choice) {
            case "1" -> {
                String sql = "SELECT name FROM user JOIN user_project ON user_project.id = user.id JOIN project ON project.id = project.id";
                try (
                        PreparedStatement stmt = DatabaseConnection.getConnection().prepareStatement(sql);
                        ResultSet rs = stmt.executeQuery()
                ) {

                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }


            }
        }
    }
}
