package org.teamflow.screens;

import org.teamflow.ScreenManager;
import org.teamflow.controllers.ChatController;
import org.teamflow.controllers.ProjectController;
import org.teamflow.controllers.UserController;
import org.teamflow.enums.ChatroomLinkType;
import org.teamflow.enums.ScreenType;
import org.teamflow.interfaces.Screen;
import org.teamflow.models.Chatroom;
import org.teamflow.models.Task;
import org.teamflow.services.UserProjectRoleService;

import java.util.ArrayList;
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
        System.out.println("Enter task description: ");
        String status = scanner.nextLine();

        projectController.createTask(title, status);
    }

    private void listTasks() {
        ArrayList<String> tasks = projectController.listTasks();
        for (String task : tasks) {
            System.out.println(task);
        }
    }

    private void selectTask() {
        List<Task> tasks = projectController.getTasks();
        System.out.println("Select a task:");
        for (int i = 0; i < tasks.size(); i++) {
            System.out.println((i + 1) + ". " + tasks.get(i).getTitle());
        }

        int roleIndex;
        try {
            roleIndex = Integer.parseInt(scanner.nextLine()) - 1;
            if (roleIndex < 0 || roleIndex >= tasks.size()) {
                System.out.println("Invalid task selection.");
                return;
            }
        } catch (NumberFormatException e) {
            System.out.println("Please enter a valid number.");
            return;
        }

        Task selectedTask = tasks.get(roleIndex);
        projectController.setCurrentTask(selectedTask);

        if (projectController.getCurrentTask() != null) {
            showTaskDetailMenu();
        }
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
                    if (UserController.isScrumMaster()) {
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
        String status = task.getStatus() != null ? task.getStatus() : "Unknown status";
        String title = (task != null) ? task.getTitle() : "[No task selected]";
        System.out.println("\n===== Task: " + title + ", Status: " + status +" =====");
        System.out.println("1. Edit task");
        System.out.println("2. Assign user");
        System.out.println("3. View chatrooms");
        System.out.println("4. Create chatroom");

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

        System.out.print("Enter new status: ");
        String newStatus = scanner.nextLine();
        projectController.editTask(task.getId(), newStatus);
        task.setStatus(newStatus);
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
                projectController.assignUserToTask(selectedUser.getId());
                System.out.println(selectedUser.getUsername() + " has been assigned.");
            }
        } else {
            projectController.assignUserToTask(userController.getUserId());
            System.out.println("You have been added to this task");
        }
    }

    private void listChatrooms() {
        ChatController chatController = screenManager.getChatController();
        List<Chatroom> chatrooms = chatController.getChatroomsForTask(projectController.getCurrentTask().getId());

        System.out.println("Select a Chatroom:");

        for (int i = 0; i < chatrooms.size(); i++) {
            System.out.println((i + 1) + ". " + chatrooms.get(i).getName());
        }

        int roleIndex;
        try {
            roleIndex = Integer.parseInt(scanner.nextLine()) - 1;
            if (roleIndex < 0 || roleIndex >= chatrooms.size()) {
                System.out.println("Invalid chatroom selection.");
                return;
            }
        } catch (NumberFormatException e) {
            System.out.println("Please enter a valid number.");
            return;
        }

        Chatroom selectedChatroom = chatrooms.get(roleIndex);
        chatController.setCurrentChatroom(selectedChatroom);

        if (chatController.getCurrentChatroom() != null) {
            screenManager.switchTo(ScreenType.CHATROOM);
        }
    }

    private void createChatroom() {
        ChatController chatController = screenManager.getChatController();
        System.out.println("What is the name of the Chatroom: ");
        String chatroomname = scanner.nextLine();

        Chatroom chatroom = new Chatroom(chatroomname);
        chatroom.setLinkType(ChatroomLinkType.TASK);
        chatroom.setLinkedEntityId(projectController.getCurrentTask().getId());
        chatController.createChatroom(chatroom);
    }

    private void deleteTask() {
        if (!UserController.isScrumMaster()) {
            System.out.println("Only Scrum Masters can delete tasks.");
        }

        Task task = projectController.getCurrentTask();

        if (task == null) {
            System.out.println("No task selected.");
        }

        assert task != null;
        projectController.deleteById("Task", task.getId());
    }
}
