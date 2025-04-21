package org.teamflow.screens;

import org.teamflow.ScreenManager;
import org.teamflow.controllers.ProjectController;
import org.teamflow.enums.ScreenType;
import org.teamflow.abstracts.Screen;
import org.teamflow.models.Project;
import org.teamflow.models.ProjectCreationResult;
import org.teamflow.services.UserProjectRoleService;
import java.util.ArrayList;

import static org.teamflow.ScreenManager.clearScreen;

public class DashboardScreen extends Screen {

    public DashboardScreen(ScreenManager screenManager) {
        super(screenManager);
    }

    @Override
    public void show() {
        while (true) {
            clearScreen();
            printBreadcrumb("Dashboard");
            System.out.println();
            System.out.println("1. Create a new project");
            System.out.println("2. Join a project");
            System.out.println("3. View joined projects");
            System.out.println("4. Logout");
            System.out.println("5. Exit");
            System.out.println("0. Delete account");

            String choice = scanner.nextLine();

            switch (choice) {
                case "0" -> {
                    userController.deleteUser();
                    screenManager.switchTo(ScreenType.LOGIN);
                    return;
                }
                case "1" -> createProject();
                case "2" -> joinProject();
                case "3" -> openProject();
                case "4" -> {
                    userController.logout();
                    screenManager.switchTo(ScreenType.LOGIN);
                    return;
                }
                case "5" -> {
                    System.out.println("Goodbye!");
                    System.exit(0);
                }
                default -> setAlertMessage("Invalid choice.");
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
            setAlertMessage("Project created successfully.");
            UserProjectRoleService.assignRoleToUser(userController.getUserId(), project.getId(), "Scrum Master");

            screenManager.switchTo(ScreenType.PROJECT);
        } else if (result.getStatus() == 2) {
            setAlertMessage("Project already exists.");
        } else {
            setAlertMessage("Error creating project.");
        }
    }

    public void joinProject() {
        System.out.println("Which project to join?");
        ArrayList<Project> projects = displayAllProjects();

        int choice = scanner.nextInt();

        boolean exists = projects.stream().anyMatch(p -> p.getId() == choice);

        if (exists) {
            UserProjectRoleService.assignRoleToUser(userController.getUserId(), choice, "Developer");
        } else {
            setAlertMessage("Project not found.");
        }
    }

    public ArrayList<Project> displayAllProjects() {
        ArrayList<Project> allProjects = projectController.listProjects();
        System.out.println("All Projects:");
        for (Project project : allProjects) {
            System.out.println(project.getId() + ": " + project.getName() + " - " + project.getDescription());
        }
        return allProjects;
    }

    public void openProject() {
        for (Project project : userController.getProjects()) {
            System.out.println(project.getId() + ": " + project.getName() + " - " + project.getDescription());
        }

        System.out.println("Which project to open?");

        int projectId = scanner.nextInt();
        if (projectController.setCurrentProject(projectId)) {
            screenManager.switchTo(ScreenType.PROJECT);
        }
    }
}
