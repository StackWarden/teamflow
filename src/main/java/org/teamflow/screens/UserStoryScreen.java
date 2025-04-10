package org.teamflow.screens;

import org.teamflow.ScreenManager;
import org.teamflow.controllers.ProjectController;
import org.teamflow.controllers.UserController;
import org.teamflow.interfaces.Screen;
import org.teamflow.models.UserStory;
import org.teamflow.enums.ScreenType;
import org.teamflow.services.UserProjectRoleService;

import java.util.ArrayList;
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
                case "4" -> editUserStory();
                case "5" -> {
                    if (UserProjectRoleService.isScrumMaster(userController.getUserId(), projectController.getCurrentProjectId())) {
                        deleteUserStory();
                    } else {
                        System.out.println("Only Scrum Masters can delete user stories.");
                    }
                }
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
        System.out.println("Which story do you want to delete?");
        listUserStories();
        System.out.print("Enter number of user story: ");
        int storyId = scanner.nextInt();
        projectController.deleteUserStory(storyId);
    }

    public void listUserStories() {
        ArrayList<String> stories = projectController.listUserStories();
        for (String story : stories) {
            System.out.println(story);
        }
    }

    private void selectUserStory() {
        System.out.println("[TODO] Select a user story from list by number or ID");
        // UserStory selected = ...
        // projectController.setCurrentUserStory(selected);
        showUserStoryDetailMenu();
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
                case "4" -> editStory();
                case "5" -> {
                    if (isScrumMaster()) {
                        if (deleteStory()) {
                            running = false;
                        }
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

    private void editStory() {
        System.out.println("[TODO] Edit Story");
    }

    private boolean deleteStory() {
        if (!isScrumMaster()) {
            System.out.println("Only Scrum Masters can delete stories.");
            return false;
        }

        UserStory story = projectController.getCurrentUserStory();
        if (story == null) {
            System.out.println("No story selected.");
            return false;
        }

        System.out.print("Type EXACTLY the story description to confirm deletion: ");
        String input = scanner.nextLine();

        if (input.equals(story.getDescription())) {
            System.out.println("[TODO] Delete user story from database: " + story.getId());
            // projectController.deleteUserStory(story.getId());
            return true;
        } else {
            System.out.println("Confirmation failed. Story was not deleted.");
            return false;
        }
    }
    private boolean isScrumMaster() {
        return org.teamflow.services.UserProjectRoleService.isScrumMaster(userController.getUserId(), projectController.getCurrentProjectId());
    }
}
