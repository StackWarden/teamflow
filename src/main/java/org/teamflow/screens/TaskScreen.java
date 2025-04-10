package org.teamflow.screens;

import org.teamflow.ScreenManager;
import org.teamflow.controllers.ProjectController;
import org.teamflow.controllers.UserController;
import org.teamflow.enums.ScreenType;
import org.teamflow.interfaces.Screen;
import org.teamflow.models.Task;
import org.teamflow.services.UserProjectRoleService;

import java.util.List;
import java.util.Scanner;

public class TaskScreen implements Screen {

    private final Scanner scanner;
    private final ProjectController projectController;
    private final UserController userController;
    private final ScreenManager screenManager;

    public TaskScreen(Scanner scanner, ProjectController projectController, UserController userController, ScreenManager screenManager) {
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
                case "1" -> createTask();
                case "2" -> listTasks();
                case "3" -> selectTask();
                case "0" -> {
                    System.out.println("Returning to story...");
                    running = false;
                }
                default -> System.out.println("Invalid input.");
            }
        }
    }

    private void printMenu() {
        System.out.println("\n===== Tasks for Story: " +
                projectController.getCurrentUserStory().getDescription() + " =====");
        System.out.println("1. Create task");
        System.out.println("2. View tasks");
        System.out.println("3. Select task");
        System.out.println("0. Back");
    }

    private void createTask() {
        System.out.print("Enter task title: ");
        String title = scanner.nextLine();

        System.out.println("[TODO] Create task: " + title);
        // projectController.createTask(currentStoryId, title);
    }

    private void listTasks() {
        System.out.println("[TODO] List tasks for selected story");
        // List<Task> tasks = projectController.getTasksForStory(storyId);
    }

    private void selectTask() {
        System.out.println("[TODO] Select a task (by number or ID) and set currentTask in controller");
        // projectController.setCurrentTask(task);
        showTaskDetailMenu();
    }

    private void showTaskDetailMenu() {
        boolean running = true;

        while (running) {
            printTaskDetailMenu();
            String input = scanner.nextLine();

            switch (input) {
                case "1" -> editTask();
                case "2" -> assignUser();
                case "3" -> listChatrooms();
                case "4" -> createChatroom();
                case "5" -> {
                    if (isScrumMaster()) {
                        deleteTask();
                    }
                }
                case "0" -> running = false;
                default -> System.out.println("Invalid input.");
            }
        }
    }

    private void printTaskDetailMenu() {
        Task task = projectController.getCurrentTask();
        String title = (task != null) ? task.getTitle() : "[No task selected]";
        System.out.println("\n===== Task: " + title + " =====");
        System.out.println("1. Edit task");
        System.out.println("2. Assign user");
        System.out.println("3. View chatrooms");
        System.out.println("4. Create chatroom");
        System.out.println("5. Delete task");

        boolean isScrumMaster = UserProjectRoleService.isScrumMaster(
                userController.getUserId(),
                projectController.getCurrentProjectId()
        );

        if (isScrumMaster) {
            System.out.println("5. Delete task");
        }

        System.out.println("0. Back");
    }

    private void editTask() {
        Task task = projectController.getCurrentTask();
        if (task == null) {
            System.out.println("No task selected.");
            return;
        }

        System.out.print("Enter new title: ");
        String newTitle = scanner.nextLine();

        System.out.print("Enter new status: ");
        String newStatus = scanner.nextLine();

        System.out.println("[TODO] Update task title/status in DB");
        // projectController.updateTask(task.getId(), newTitle, newStatus);
    }

    private void assignUser() {
        Task task = projectController.getCurrentTask();
        if (task == null) {
            System.out.println("No task selected.");
            return;
        }

        boolean isScrumMaster = UserProjectRoleService.isScrumMaster(
                userController.getUserId(),
                projectController.getCurrentProjectId()
        );

        if (isScrumMaster) {
            System.out.println("Available users:");
            var users = userController.getAllUsers();
            for (int i = 0; i < users.size(); i++) {
                System.out.println((i + 1) + ". " + users.get(i).getUsername());
            }
            System.out.print("Select user to assign: ");
            int index = Integer.parseInt(scanner.nextLine()) - 1;
            if (index >= 0 && index < users.size()) {
                var selectedUser = users.get(index);
                System.out.println("[TODO] Assign user " + selectedUser.getUsername() + " to task");
                // projectController.assignUserToTask(task.getId(), selectedUser.getId());
            }
        } else {
            System.out.println("[TODO] Assigning yourself to task...");
            // projectController.assignUserToTask(task.getId(), userController.getUserId());
        }
    }

    private void listChatrooms() {
        System.out.println("[TODO] List chatrooms linked to this task");
        // chatroomController.getForTask(taskId);
    }

    private void createChatroom() {
        System.out.print("Enter chatroom name: ");
        String name = scanner.nextLine();
        System.out.println("[TODO] Create and link chatroom: " + name);
        // chatroomController.createChatroom(name, taskId, "task");
    }

    private void deleteTask() {
        if (!isScrumMaster()) {
            System.out.println("Only Scrum Masters can delete tasks.");
        }

        Task task = projectController.getCurrentTask();

        if (task == null) {
            System.out.println("No task selected.");
        }

        assert task != null;
        projectController.deleteById("Task", task.getId());
    }

    private boolean isScrumMaster() {
        return org.teamflow.services.UserProjectRoleService.isScrumMaster(userController.getUserId(), projectController.getCurrentProjectId());
    }
}
