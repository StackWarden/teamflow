package org.teamflow.screens;

import org.teamflow.ScreenManager;
import org.teamflow.controllers.ChatController;
import org.teamflow.enums.ChatroomLinkType;
import org.teamflow.abstracts.Screen;
import org.teamflow.models.Chatroom;
import org.teamflow.models.UserStory;
import org.teamflow.enums.ScreenType;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class UserStoryScreen extends Screen {

    public UserStoryScreen(ScreenManager screenManager) {
        super(screenManager);
    }

    @Override
    public void show() {
        AtomicBoolean running = new AtomicBoolean(true);

        while (running.get()) {
            printBreadcrumb("Dashboard", "Project", "Epic", "UserStory");

            List<MenuOption> menu = new ArrayList<>();
            menu.add(new MenuOption("Create user story", this::createUserStory));
            menu.add(new MenuOption("View user stories", this::listUserStories));
            menu.add(new MenuOption("Select user story", this::selectUserStory));

            displayMenu(menu, () -> {
                System.out.println("Returning to epic screen...");
                running.set(false);
            });
        }
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
        printBreadcrumb("Dashboard", "Project", "Epic", "UserStory");

        boolean isScrumMaster = userController.isScrumMaster(projectController.getCurrentProjectId());

        List<MenuOption> options = new ArrayList<>();
        options.add(new MenuOption("Go to task screen", () -> screenManager.switchTo(ScreenType.TASK)));
        options.add(new MenuOption("View linked chatrooms", this::listChatrooms));
        options.add(new MenuOption("Create chatroom", this::createChatroom));
        options.add(new MenuOption("Edit story", this::editUserStory, isScrumMaster));
        options.add(new MenuOption("Delete story", this::deleteUserStory, isScrumMaster));

        displayMenu(options, () -> {});
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
