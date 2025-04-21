package org.teamflow.screens;

import org.teamflow.ScreenManager;
import org.teamflow.controllers.ProjectController;
import org.teamflow.controllers.UserController;
import org.teamflow.enums.ScreenType;
import org.teamflow.abstracts.Screen;
import org.teamflow.models.Project;
import org.teamflow.services.UserProjectRoleService;

import java.util.ArrayList;
import java.util.Scanner;

import static org.teamflow.ScreenManager.clearScreen;

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
            clearScreen();

            printMenu();
            String input = scanner.nextLine().trim();

            switch (input) {
                case "1" -> goToEpicScreen();
                case "2" -> goToSprintScreen();
                case "3" -> goToChatroomScreen();
                case "4" -> {
                    if (UserProjectRoleService.isScrumMaster(userController.getUserId(), projectController.getCurrentProjectId())) {
                        goToMemberScreen();
                    } else {
                        System.out.println("Only Scrum Masters can manage members.");
                    }
                }
                case "5" -> editProjectUI();
                case "6" -> {
                    if (deleteProject()) {
                        running = false;
                    }
                }
                case "0" -> {
                    System.out.println("Returning to dashboard...");
                    running = false;
                }
                case "" -> {

                }
                default -> {
                    System.out.println();
                    System.out.println("Invalid input. Please try again.");
                }
            }
        }
    }

    protected void printMenu() {
        boolean isScrumMaster = UserProjectRoleService.isScrumMaster(userController.getUserId(), projectController.getCurrentProjectId());

        System.out.println("\n===== " + projectController.getProjectNameAndUserRole(userController.getLoggedUser()) + " =====");
        System.out.println("1. View Epics");
        System.out.println("2. View Sprints");
        System.out.println("3. Open Chatrooms");
        if (isScrumMaster) {
            System.out.println("4. Manage Project Members");
            System.out.println("5. Edit Project Name");
            System.out.println("6. Delete Project");
        }
        System.out.println("0. Back to Dashboard");
    }

    protected void goToEpicScreen() {
         screenManager.switchTo(ScreenType.EPIC);
    }

    protected void goToSprintScreen() {
        System.out.println("TODO: Open SprintScreen");
        // screenManager.switchTo(ScreenType.SPRINT);
    }

    protected void goToChatroomScreen() {
         screenManager.switchTo(ScreenType.CHATROOM);
    }

    protected void goToMemberScreen() {
         screenManager.switchTo(ScreenType.PROJECT_MEMBERS);
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
    public void displayScrumMasterProjects() {
        int uid = userController.getUserId();
        ArrayList<Project> scrumMasterProjects = projectController.listProjectsWhereScrummaster(uid);
        System.out.println("Your Scrum Master Projects:");
        for (Project project : scrumMasterProjects) {
            System.out.println(project.getId() + ": " + project.getName() + " - " + project.getDescription());
        }
    }
}
