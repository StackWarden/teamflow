package org.teamflow.screens;

import org.teamflow.ScreenManager;
import org.teamflow.controllers.ProjectController;
import org.teamflow.enums.ScreenType;
import org.teamflow.interfaces.Screen;
import org.teamflow.controllers.UserController;
import org.teamflow.models.Project;
import org.teamflow.models.ProjectCreationResult;
import org.teamflow.services.UserProjectRoleService;
import java.util.ArrayList;
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
            System.out.println("8. Edit a project name and description");
            System.out.println("9. Remove a user from a project");
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
                case "3" -> System.out.println("TODO: list projects");
                case "4" -> {
                    userController.logout();
                    screenManager.switchTo(ScreenType.LOGIN);
                    return;
                }
                case "5" -> {
                    System.out.println("Goodbye!");
                    System.exit(0);
                }
                case "8" -> {
                    editProjectUI();
                }
                case "9" -> {
                    removeUserFromProjectUI();
                }
                default -> System.out.println();
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
            UserProjectRoleService.assignRoleToUser(userController.getUserId(), project.getId(), "Scrum Master");

            screenManager.switchTo(ScreenType.PROJECT);
        } else if (result.getStatus() == 2) {
            System.out.println("Project already exists.");
        } else {
            System.out.println("Error creating project.");
        }
    }

    private void removeUserFromProjectUI() {
        System.out.print("Enter username to remove from project: ");
        String username = scanner.nextLine();

        System.out.print("Enter project name: ");
        String projectName = scanner.nextLine();

        boolean success = projectController.removeUserFromProjectByName(username, projectName);

        if (success) {
            System.out.println("User removed from project.");
        } else {
            System.out.println("Could not remove user from project.");
        }
    }

    public void joinProject() {
        System.out.println("Which project to join?");
        ArrayList<Project> projects = displayAllProjects();
        for (Project project : projects) {
            if (!UserProjectRoleService.isMemberOfProject(userController.getUserId(), project.getId())) {
                System.out.println(project.getId() + ". " + project.getName());
            }
        }

        int choice = scanner.nextInt();

        boolean exists = projects.stream().anyMatch(p -> p.getId() == choice);

        if (exists) {
            UserProjectRoleService.assignRoleToUser(userController.getUserId(), choice, "Developer");
        } else {
            System.out.println("Project does not exist.");
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

    public void displayScrumMasterProjects() {
        int uid = userController.getUserId();
        ArrayList<Project> scrumMasterProjects = projectController.listProjectsWhereScrummaster(uid);
        System.out.println("Your Scrum Master Projects:");
        for (Project project : scrumMasterProjects) {
            System.out.println(project.getId() + ": " + project.getName() + " - " + project.getDescription());
        }
    }

    public void editProjectUI() {
        System.out.println("Select the Project ID to edit:");
        displayScrumMasterProjects();

        System.out.print("Enter Project ID: ");
        int projectId;
        try {
            projectId = Integer.parseInt(scanner.nextLine());
        } catch (NumberFormatException e) {
            System.out.println("Invalid project ID.");
            return;
        }

        Project projectToEdit = projectController.getProjectById(projectId);

        if (projectToEdit == null) {
            System.out.println("Project not found or you're not the Scrum Master of this project.");
            return;
        }

        System.out.println("Editing Project: " + projectToEdit.getName());
        System.out.println("Current Description: " + projectToEdit.getDescription());

        System.out.print("Enter new project name (leave blank to keep current): ");
        String newName = scanner.nextLine();
        if (newName.isBlank()) newName = projectToEdit.getName();

        System.out.print("Enter new project description (leave blank to keep current): ");
        String newDescription = scanner.nextLine();
        if (newDescription.isBlank()) newDescription = projectToEdit.getDescription();

        boolean success = projectController.editProject(projectId, newName, newDescription);
        if (success) {
            System.out.println("Project updated successfully!");
        } else {
            System.out.println("Failed to update the project.");
        }
    }

}
