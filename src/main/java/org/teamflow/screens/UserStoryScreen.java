package org.teamflow.screens;

import org.teamflow.ScreenManager;
import org.teamflow.controllers.ChatController;
import org.teamflow.controllers.ProjectController;
import org.teamflow.controllers.UserController;
import org.teamflow.enums.ChatroomLinkType;
import org.teamflow.abstracts.Screen;
import org.teamflow.models.Chatroom;
import org.teamflow.models.UserStory;
import org.teamflow.enums.ScreenType;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class UserStoryScreen extends Screen {

    public UserStoryScreen(ScreenManager screenManager) {
        super(screenManager);
    }

    @Override
    public void show() {
        boolean running = true;

        while (running) {
            printBreadcrumb("Dashboard", "Project", "Epic", "UserStory");
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
                default -> setAlertMessage("Invalid input. Try again.");
            }
        }
    }

    private void printMenu() {
        System.out.println("User Stories for Epic: " + projectController.getCurrentEpic().getTitle());
        System.out.println();
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
                setAlertMessage("Invalid story selection.");
                return;
            }
        } catch (NumberFormatException e) {
            setAlertMessage("Please enter a valid number.");
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
            printBreadcrumb("Dashboard", "Project", "Epic", "UserStory");
            printUserStoryDetailMenu();
            String input = scanner.nextLine();

            switch (input) {
                case "1" -> screenManager.switchTo(ScreenType.TASK);
                case "2" -> listChatrooms();
                case "3" -> createChatroom();
                case "4" -> {
                    if (userController.isScrumMaster(projectController.getCurrentProjectId())) {
                        editUserStory();
                    } else {
                        setAlertMessage("Only a Scrum Master can edit an User Story.");
                    }
                }
                case "5" -> {
                    if (userController.isScrumMaster(projectController.getCurrentProjectId())) {
                        deleteUserStory();
                    } else {
                        setAlertMessage("Only a Scrum Master can edit a User Story.");
                    }
                }
                case "0" -> running = false;
                default -> setAlertMessage("Invalid input.");
            }
        }
    }

    private void printUserStoryDetailMenu() {
        UserStory story = projectController.getCurrentUserStory();
        String title = (story != null) ? story.getDescription() : "[No story selected]";

        System.out.println(title);
        System.out.println();
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
        ChatController chatController = screenManager.getChatController();
        List<Chatroom> chatrooms = chatController.getChatroomsForUserStory(projectController.getCurrentUserStory().getId());

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


    private void createChatroom() {;
        ChatController chatController = screenManager.getChatController();
        System.out.println("What is the name of the Chatroom: ");
        String chatroomname = scanner.nextLine();

        Chatroom chatroom = new Chatroom(chatroomname);
        chatroom.setLinkType(ChatroomLinkType.STORY);
        chatroom.setLinkedEntityId(projectController.getCurrentUserStory().getId());
        chatController.createChatroom(chatroom);
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
        if (!userController.isScrumMaster(projectController.getCurrentProjectId())) {
            setAlertMessage("Only Scrum Masters can delete stories.");
            return;
        }

        UserStory story = projectController.getCurrentUserStory();

        if (story == null) {
            setAlertMessage("No story selected.");
            return;
        }

        projectController.deleteById("UserStory", story.getId());
    }
}
