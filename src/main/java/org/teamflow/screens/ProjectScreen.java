package org.teamflow.screens;

import org.teamflow.ScreenManager;
import org.teamflow.enums.ScreenType;
import org.teamflow.abstracts.Screen;
import org.teamflow.models.Project;
import org.teamflow.services.UserProjectRoleService;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.teamflow.ScreenManager.clearScreen;

public class ProjectScreen extends Screen {

    public ProjectScreen(ScreenManager screenManager) {
        super(screenManager);
    }

    @Override
    public void show() {
        boolean isScrumMaster = userController.isScrumMaster(projectController.getCurrentProjectId());

        List<MenuOption> menuList = new ArrayList<>();
        menuList.add(new MenuOption("View Epics", this::goToEpicScreen));
        menuList.add(new MenuOption("View Sprints", this::goToSprintScreen));
        menuList.add(new MenuOption("Open Chatrooms", this::goToChatroomScreen));
        menuList.add(new MenuOption("Manage Project Members", this::goToMemberScreen, isScrumMaster));
        menuList.add(new MenuOption("Edit Project Name", this::editProjectUI, isScrumMaster));
        menuList.add(new MenuOption("Delete Project", this::deleteProject, isScrumMaster));

        clearScreen();
        printBreadcrumb("Dashboard", "Project", screenManager.getProjectController().getCurrentProjectName());
        displayMenu(menuList, () -> {
            System.out.println("Returning to dashboard...");
            projectController.resetCurrentProject();
            screenManager.switchTo(ScreenType.DASHBOARD);
        });
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

    protected void deleteProject() {
        boolean isScrumMaster = UserProjectRoleService.isScrumMaster(userController.getUserId(), projectController.getCurrentProjectId());

        if (!isScrumMaster) {
            setAlertMessage("You do not have permission to delete this project");
            return;
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
            setAlertMessage("Project deleted.");
            return;
        }

        setAlertMessage("Project name did not match. Deletion cancelled.");
    }

    public void editProjectUI() {
        System.out.println("Select the Project ID to edit:");
        displayScrumMasterProjects();

        System.out.print("Enter Project ID: ");
        int projectId;
        try {
            projectId = Integer.parseInt(scanner.nextLine());
        } catch (NumberFormatException e) {
            setAlertMessage("Project ID must be an integer.");
            return;
        }

        Project projectToEdit = projectController.getProjectById(projectId);

        if (projectToEdit == null) {
            setAlertMessage("Project does not exist.");
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
            setAlertMessage("Project updated successfully!");
        } else {
            setAlertMessage("Failed to update the project.");
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
