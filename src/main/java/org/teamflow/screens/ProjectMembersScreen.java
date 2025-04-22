package org.teamflow.screens;

import org.teamflow.ScreenManager;
import org.teamflow.abstracts.Screen;
import org.teamflow.models.Role;
import org.teamflow.models.User;
import org.teamflow.services.UserProjectRoleService;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class ProjectMembersScreen extends Screen {

    public ProjectMembersScreen(ScreenManager screenManager) {
        super(screenManager);
    }

    @Override
    public void show() {
        AtomicBoolean running = new AtomicBoolean(true);
        printBreadcrumb("Dashboard", "Project", "Project Members");

        List<MenuOption> options = new ArrayList<>();
        options.add(new MenuOption("View all members", this::listMembers));
        options.add(new MenuOption("Add member to project", this::addMember));
        options.add(new MenuOption("Change member role", this::changeUserRole));
        options.add(new MenuOption("Remove member", this::removeMember));

        displayMenu(options, () -> {
            System.out.println("Returning to project screen...");
            running.set(false);
        });
    }

    private void listMembers() {
        List<User> members = projectController.getProjectMembers(projectController.getCurrentProjectId());

        if (members.isEmpty()) {
            setAlertMessage("No members found in this project.");
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
            setAlertMessage("No users found.");
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
                setAlertMessage("Invalid selection.");
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
                setAlertMessage("User added to project successfully.");
            } else {
                setAlertMessage("Failed to add user to project.");
            }

        } catch (NumberFormatException e) {
            setAlertMessage("Invalid input. Please enter a valid number.");
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
            setAlertMessage("Invalid user ID.");
            return;
        }

        User selectedUser = userController.getUserById(userId);
        if (selectedUser == null) {
            setAlertMessage("User not found.");
            return;
        }

        if (selectedUser.getId() == userController.getLoggedUser().getId())
        {
            setAlertMessage("You can not change your own role.");
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
                setAlertMessage("Invalid role selection.");
                return;
            }
        } catch (NumberFormatException e) {
            setAlertMessage("Please enter a valid number.");
            return;
        }

        Role selectedRole = roles.get(roleIndex);
        projectController.changeUserRoleInProject(userId, selectedRole);
        setAlertMessage("Role updated for user " + selectedUser.getUsername());
    }

    private void removeMember() {
        if (!isScrumMaster()) return;

        listMembers();

        System.out.print("Which user do you want to remove from the project? ");

        int userId;
        try {
            userId = Integer.parseInt(scanner.nextLine());
        } catch (NumberFormatException e) {
            setAlertMessage("Invalid input. Please enter a valid user ID.");
            return;
        }

        if (userId == userController.getLoggedUser().getId()) {
            setAlertMessage("You cannot remove yourself.");
            return;
        }

        projectController.removeUserFromProject(userId);
    }

    private boolean isScrumMaster() {
        boolean result = UserProjectRoleService.isScrumMaster(userController.getUserId(), projectController.getCurrentProjectId());
        if (!result) {
            setAlertMessage("Only Scrum Masters can perform this action.");
        }
        return result;
    }
}
