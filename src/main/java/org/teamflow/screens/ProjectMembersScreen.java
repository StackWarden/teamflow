package org.teamflow.screens;

import org.teamflow.ScreenManager;
import org.teamflow.controllers.ProjectController;
import org.teamflow.controllers.UserController;
import org.teamflow.abstracts.Screen;
import org.teamflow.models.Role;
import org.teamflow.models.User;
import org.teamflow.services.UserProjectRoleService;

import java.util.List;
import java.util.Scanner;

public class ProjectMembersScreen implements Screen {

    private final Scanner scanner;
    private final ScreenManager screenManager;
    private final UserController userController;
    private final ProjectController projectController;

    public ProjectMembersScreen(Scanner scanner, ProjectController projectController, UserController userController, ScreenManager screenManager) {
        this.scanner = scanner;
        this.screenManager = screenManager;
        this.userController = userController;
        this.projectController = projectController;
    }

    @Override
    public void show() {
        boolean running = true;

        while (running) {
            printMenu();

            String input = scanner.nextLine();
            switch (input) {
                case "1" -> listMembers();
                case "2" -> addMember();
                case "3" -> changeUserRole();
                case "4" -> removeMember();
                case "0" -> {
                    System.out.println("Returning to project screen...");
                    running = false;
                }
                default -> System.out.println();
            }
        }
    }

    private void printMenu() {
        System.out.println("\n===== Project Members =====");
        System.out.println("1. View all members");
        System.out.println("2. Add member to project");
        System.out.println("3. Change member role");
        System.out.println("4. Remove member");
        System.out.println("0. Back");
    }

    private void listMembers() {
        List<User> members = projectController.getProjectMembers(projectController.getCurrentProjectId());

        if (members.isEmpty()) {
            System.out.println("No members found in this project.");
            return;
        }

        System.out.println("\nProject Members:");
        for (User user : members) {
            String role = UserProjectRoleService.getUserRoleForProject(user.getId(), projectController.getCurrentProjectId());
            System.out.println(user.getId() + " - " + user.getUsername() + " (" + role + ")");
        }
    }

    private void addMember() {
        if (!isScrumMaster()) return;

        var allUsers = userController.getAllUsers();

        if (allUsers.isEmpty()) {
            System.out.println("No users found.");
            return;
        }

        System.out.println("Select a user to add to the project:");
        for (int i = 0; i < allUsers.size(); i++) {
            var user = allUsers.get(i);
            System.out.println((i + 1) + ". " + user.getUsername() + " (ID: " + user.getId() + ")");
        }

        System.out.print("Enter the number of the user to add: ");
        String input = scanner.nextLine();

        try {
            int selectedIndex = Integer.parseInt(input) - 1;

            if (selectedIndex < 0 || selectedIndex >= allUsers.size()) {
                System.out.println("Invalid selection.");
                return;
            }

            var selectedUser = allUsers.get(selectedIndex);
            var members = projectController.getCurrentProject().getMembers();

            boolean alreadyInProject = members.stream()
                    .anyMatch(user -> user.getId() == selectedUser.getId());

            if (alreadyInProject) {
                System.out.println("User is already in project.");
                return;
            }

            boolean success = userController.addUserToProject(
                    selectedUser.getId(),
                    projectController.getCurrentProjectId()
            );

            if (success) {
                System.out.println("User added to project successfully.");
            } else {
                System.out.println("Failed to add user to project.");
            }

        } catch (NumberFormatException e) {
            System.out.println("Invalid input. Please enter a valid number.");
        }
    }

    private void changeUserRole() {
        if (!isScrumMaster()) return;

        // Gebruik bestaande methode om leden te tonen
        listMembers();

        System.out.print("Enter the user ID you want to update: ");
        int userId;
        try {
            userId = Integer.parseInt(scanner.nextLine());
        } catch (NumberFormatException e) {
            System.out.println("Invalid user ID.");
            return;
        }

        User selectedUser = userController.getUserById(userId);
        if (selectedUser == null) {
            System.out.println("User not found.");
            return;
        }

        if (selectedUser.getId() == userController.getLoggedUser().getId())
        {
            System.out.println("You can not change your own role.");
            return;
        }

        // Toon alle rollen
        List<Role> roles = projectController.getAllRoles();
        System.out.println("Select a new role:");
        for (int i = 0; i < roles.size(); i++) {
            System.out.println((i + 1) + ". " + roles.get(i).getRoleName());
        }

        int roleIndex;
        try {
            roleIndex = Integer.parseInt(scanner.nextLine()) - 1;
            if (roleIndex < 0 || roleIndex >= roles.size()) {
                System.out.println("Invalid role selection.");
                return;
            }
        } catch (NumberFormatException e) {
            System.out.println("Please enter a valid number.");
            return;
        }

        Role selectedRole = roles.get(roleIndex);
        projectController.changeUserRoleInProject(userId, selectedRole);
        System.out.println("Role updated for user " + selectedUser.getUsername());
    }

    private void removeMember() {
        if (!isScrumMaster()) return;

        listMembers();

        System.out.print("Which user do you want to remove from the project? ");

        int userId;
        try {
            userId = Integer.parseInt(scanner.nextLine());
        } catch (NumberFormatException e) {
            System.out.println("Invalid input. Please enter a valid user ID.");
            return;
        }

        if (userId == userController.getLoggedUser().getId()) {
            System.out.println("You cannot remove yourself.");
            return;
        }

        projectController.removeUserFromProject(userId);
    }

    private boolean isScrumMaster() {
        boolean result = UserProjectRoleService.isScrumMaster(userController.getUserId(), projectController.getCurrentProjectId());
        if (!result) {
            System.out.println("Only Scrum Masters can perform this action.");
        }
        return result;
    }
}
