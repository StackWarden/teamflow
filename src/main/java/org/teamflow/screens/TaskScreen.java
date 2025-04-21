package org.teamflow.screens;

import org.teamflow.ScreenManager;
import org.teamflow.controllers.ChatController;
import org.teamflow.controllers.ProjectController;
import org.teamflow.controllers.UserController;
import org.teamflow.enums.ChatroomLinkType;
import org.teamflow.enums.ScreenType;
import org.teamflow.abstracts.Screen;
import org.teamflow.models.Chatroom;
import org.teamflow.models.Task;
import org.teamflow.services.UserProjectRoleService;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class TaskScreen extends Screen {

    public TaskScreen(ScreenManager screenManager) {
        super(screenManager);
    }

    @Override
    public void show() {
        boolean running = true;

        while (running) {
            printBreadcrumb("Dashboard", "Project", "Epic", "UserStory", "Task");
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
                default -> setAlertMessage("Invalid input.");
            }
        }
    }

    private void printMenu() {
        System.out.println("Tasks for Story: " +
                projectController.getCurrentUserStory().getDescription());
        System.out.println();
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
                setAlertMessage("Invalid task selection.");
                return;
            }
        } catch (NumberFormatException e) {
            setAlertMessage("Please enter a valid number.");
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
            printBreadcrumb("Dashboard", "Project", "Epic", "UserStory", "Task");
            printTaskDetailMenu();
            String input = scanner.nextLine();

            switch (input) {
                case "1" -> {
                    if (userController.isScrumMaster(projectController.getCurrentProjectId())) {
                        editTask();
                    } else {
                        setAlertMessage("Only a Scrum Master can edit an Epic.");
                    }
                }
                case "2" -> assignUser();
                case "3" -> removeUserFromTask();
                case "4" -> listChatrooms();
                case "5" -> createChatroom();
                case "6" -> {
                    if (userController.isScrumMaster(projectController.getCurrentProjectId())) {
                        deleteTask();
                    } else {
                        setAlertMessage("Only a Scrum Master can edit a Task.");
                    }
                }
                case "0" -> running = false;
                default -> setAlertMessage("Invalid input.");
            }
        }
    }

    private void printTaskDetailMenu() {
        Task task = projectController.getCurrentTask();
        String status = task.getStatus() != null ? task.getStatus() : "Unknown status";
        String title = (task != null) ? task.getTitle() : "[No task selected]";
        System.out.println(title + "( " + status + " )");
        System.out.println();
        System.out.println("1. Edit task");
        System.out.println("2. Assign user");
        System.out.println("3. Remove user from task");
        System.out.println("4. View chatrooms");
        System.out.println("5. Create chatroom");

        boolean isScrumMaster = UserProjectRoleService.isScrumMaster(
                userController.getUserId(),
                projectController.getCurrentProjectId()
        );

        if (isScrumMaster) {
            System.out.println("6. Delete task");
        }

        System.out.println("0. Back");
    }

    private void editTask() {
        Task task = projectController.getCurrentTask();
        if (task == null) {
            setAlertMessage("No task selected.");
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
           setAlertMessage("No task selected.");
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
                if (projectController.assignUserToTask(selectedUser.getId()) == 1){
                    setAlertMessage(selectedUser.getUsername() + " has been assigned.");
                } else {
                    setAlertMessage(selectedUser.getUsername() + " has already been assigned.");
                }
            }
        } else {
            if (projectController.assignUserToTask(userController.getUserId()) == 1){
                setAlertMessage("You have been assigned to this task.");
            } else {
                setAlertMessage("You were already assigned to this task.");
            }
        }
    }


    private void removeUserFromTask() {
        boolean isScrumMaster = UserProjectRoleService.isScrumMaster(
                userController.getUserId(),
                projectController.getCurrentProjectId()
        );
        if (isScrumMaster) {

            projectController.assignedUsers();

            System.out.print("Which user do you want to remove from the task? ");

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

            projectController.removeUserFromTask(userId);
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
                setAlertMessage("Invalid chatroom selection.");
                return;
            }
        } catch (NumberFormatException e) {
            setAlertMessage("Please enter a valid number.");
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
        if (!userController.isScrumMaster(projectController.getCurrentProjectId())) {
            setAlertMessage("Only Scrum Masters can delete tasks.");
            return;
        }

        Task task = projectController.getCurrentTask();

        if (task == null) {
            setAlertMessage("No task selected.");
            return;
        }

        projectController.deleteById("Task", task.getId());
    }
}
