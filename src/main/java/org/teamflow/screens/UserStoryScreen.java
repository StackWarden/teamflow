package org.teamflow.screens;

import org.teamflow.ScreenManager;
import org.teamflow.controllers.ProjectController;
import org.teamflow.controllers.UserController;
import org.teamflow.interfaces.Screen;
import org.teamflow.models.Epic;
import org.teamflow.models.UserStory;
import org.teamflow.enums.ScreenType;
import org.teamflow.services.UserProjectRoleService;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class UserStoryScreen implements Screen {

    private final Scanner scanner;
    private final ProjectController projectController;
    private final UserController userController;
    private final ScreenManager screenManager;

    public UserStoryScreen(Scanner scanner, ProjectController projectController, UserController userController, ScreenManager screenManager) {
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
                case "1" -> createUserStory();
                case "2" -> listUserStories();
                case "3" -> selectUserStory();
                case "0" -> {
                    System.out.println("Returning to epic screen...");
                    running = false;
                }
                default -> System.out.println("Invalid input. Try again.");
            }
        }
    }

    private void printMenu() {
        System.out.println("\n===== User Stories for Epic: " + projectController.getCurrentEpic().getTitle() + " =====");
        System.out.println("1. Create user story");
        System.out.println("2. View user stories");
        System.out.println("3. Select user story");
        System.out.println("0. Back");
    }

    private void createUserStory() {
        String description;

        System.out.print("Enter description for your user story: ");
        description = scanner.nextLine();

        projectController.createUserStory(description);
    }

    private void listUserStories() {
        ArrayList<String> stories = projectController.listUserStories();
        for (String story : stories) {
            System.out.println(story);
        }
    }

    private void selectUserStory() {
        List<UserStory> stories = projectController.getUserStories();
        System.out.println("Select a story:");
        for (int i = 0; i < stories.size(); i++) {
            System.out.println((i + 1) + ". " + stories.get(i).getDescription());
        }

        int roleIndex;
        try {
            roleIndex = Integer.parseInt(scanner.nextLine()) - 1;
            if (roleIndex < 0 || roleIndex >= stories.size()) {
                System.out.println("Invalid story selection.");
                return;
            }
        } catch (NumberFormatException e) {
            System.out.println("Please enter a valid number.");
            return;
        }

        UserStory selectedUserStory = stories.get(roleIndex);
        projectController.setCurrentUserStory(selectedUserStory);

        if (projectController.getCurrentEpic() != null) {
            showUserStoryDetailMenu();
        }
    }

    private void showUserStoryDetailMenu() {
        boolean running = true;

        while (running) {
            printUserStoryDetailMenu();
            String input = scanner.nextLine();

            switch (input) {
                case "1" -> screenManager.switchTo(ScreenType.TASK);
                case "2" -> listChatrooms();
                case "3" -> createChatroom();
                case "4" -> editUserStory();
                case "5" -> {
                    if (UserController.isScrumMaster()) {
                        deleteUserStory();
                    }
                }
                case "0" -> running = false;
                default -> System.out.println("Invalid input.");
            }
        }
    }

    private void printUserStoryDetailMenu() {
        UserStory story = projectController.getCurrentUserStory();
        String title = (story != null) ? story.getDescription() : "[No story selected]";

        System.out.println("\n===== User Story: " + title + " =====");
        System.out.println("1. Go to task screen");
        System.out.println("2. View linked chatrooms");
        System.out.println("3. Create chatroom");
        System.out.println("4. Edit story");

        boolean isScrumMaster = projectController.getCurrentProjectId() > 0 &&
                userController.getUserId() > 0 &&
                org.teamflow.services.UserProjectRoleService.isScrumMaster(userController.getUserId(), projectController.getCurrentProjectId());

        if (isScrumMaster) {
            System.out.println("5. Delete story");
        }

        System.out.println("0. Back");
    }

    private void listChatrooms() {
        System.out.println("[TODO] List all chatrooms linked to this user story");
        // Bijv: chatroomController.getForStory(storyId)
    }

    private void createChatroom() {
        System.out.print("Enter chatroom name: ");
        String name = scanner.nextLine();
        System.out.println("[TODO] Create and link chatroom: " + name);
        // chatController.createAndLink(name, storyId, "user_story")
    }

    private void editUserStory() {
        System.out.println("Which story do you want to edit?");

        listUserStories();
        System.out.print("Enter number of user story: ");
        int storyId = scanner.nextInt();

        String description;
        System.out.print("Enter description for your user story: ");
        description = scanner.nextLine();

        projectController.editUserStory(description, storyId);
    }

    private void deleteUserStory() {
        if (!UserController.isScrumMaster()) {
            System.out.println("Only Scrum Masters can delete stories.");
        }

        UserStory story = projectController.getCurrentUserStory();

        if (story == null) {
            System.out.println("No story selected.");
        }

        assert story != null;
        projectController.deleteById("UserStory", story.getId());
    }


}
