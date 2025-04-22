package org.teamflow.screens;

import org.teamflow.ScreenManager;
import org.teamflow.enums.ScreenType;
import org.teamflow.abstracts.Screen;
import org.teamflow.models.Project;
import org.teamflow.models.ProjectCreationResult;
import org.teamflow.services.UserProjectRoleService;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.teamflow.ScreenManager.clearScreen;

public class DashboardScreen extends Screen {

    public DashboardScreen(ScreenManager screenManager) {
        super(screenManager);
    }

    @Override
    public void show() {
        clearScreen();
        printBreadcrumb("Dashboard");
        displayMenu(List.of(
                new MenuOption("Create a new project", this::createProject),
                new MenuOption("Join a project", this::joinProject),
                new MenuOption("View joined projects", this::openProject),
                new MenuOption("Logout", () -> {
                    userController.logout();
                    screenManager.switchTo(ScreenType.LOGIN);
                }),
                new MenuOption("Exit", () -> {
                    System.out.println("Goodbye!");
                    System.exit(0);
                }),
                new MenuOption("Delete account", () -> {
                    userController.deleteUser();
                    screenManager.switchTo(ScreenType.LOGIN);
                })
        ), null);
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
        scanner.nextLine();

        boolean exists = projects.stream().anyMatch(p -> p.getId() == choice);

        if (exists) {
            UserProjectRoleService.assignRoleToUser(userController.getUserId(), choice, "Developer");
        } else {
            setAlertMessage("Project not found.");
            screenManager.switchTo(ScreenType.DASHBOARD);
            return;
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
        List<Project> projects = userController.getProjects();

        if (projects.isEmpty()) {
            setAlertMessage("No projects found.");
            screenManager.switchTo(ScreenType.DASHBOARD);
        }

        for (Project project : projects) {
            System.out.println(project.getId() + ": " + project.getName() + " - " + project.getDescription());
        }

        System.out.println("Which project to open?");

        int projectId = scanner.nextInt();

        if (projectController.setCurrentProject(projectId)) {
            screenManager.switchTo(ScreenType.PROJECT);
        } else {
            screenManager.switchTo(ScreenType.DASHBOARD);
        }
    }
}
