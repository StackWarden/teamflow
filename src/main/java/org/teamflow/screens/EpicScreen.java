package org.teamflow.screens;

import org.teamflow.ScreenManager;
import org.teamflow.controllers.ChatController;
import org.teamflow.controllers.ProjectController;
import org.teamflow.controllers.UserController;
import org.teamflow.enums.ChatroomLinkType;
import org.teamflow.enums.ScreenType;
import org.teamflow.interfaces.Screen;
import org.teamflow.models.Chatroom;
import org.teamflow.models.Epic;
import org.teamflow.models.Role;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.List;
import java.util.Scanner;

import static org.teamflow.ScreenManager.clearScreen;

public class EpicScreen implements Screen {

    private final Scanner scanner;
    private final ProjectController projectController;
    private final UserController userController;
    private final ScreenManager screenManager;

    public EpicScreen(Scanner scanner, ProjectController projectController, UserController userController, ScreenManager screenManager) {
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
                case "1" -> createEpic();
                case "2" -> listEpics();
                case "3" -> selectEpic();
                case "0" -> {
                    System.out.println("Returning to project screen...");
                    running = false;
                }
                default -> System.out.println("Invalid input. Try again.");
            }
        }
    }

    private void printMenu() {
        System.out.println("\n===== Epic Menu =====");
        System.out.println("1. Create new epic");
        System.out.println("2. View epics");
        System.out.println("3. Select epic");
        System.out.println("0. Back");
    }

    private void selectEpic() {
        List<Epic> epics = projectController.getEpics();
        System.out.println("Select a Epic:");
        for (int i = 0; i < epics.size(); i++) {
            System.out.println((i + 1) + ". " + epics.get(i).getTitle());
        }

        int roleIndex;
        try {
            roleIndex = Integer.parseInt(scanner.nextLine()) - 1;
            if (roleIndex < 0 || roleIndex >= epics.size()) {
                System.out.println("Invalid epic selection.");
                return;
            }
        } catch (NumberFormatException e) {
            System.out.println("Please enter a valid number.");
            return;
        }

        Epic selectedEpic = epics.get(roleIndex);
        projectController.setCurrentEpic(selectedEpic);

        if (projectController.getCurrentEpic() != null) {
            showEpicDetailsMenu();
        }
    }

    private void showEpicDetailsMenu() {
        boolean running = true;
        while (running) {
            printEpicDetailsMenu();
            String input = scanner.nextLine();

            switch (input) {
                case "1" -> screenManager.switchTo(ScreenType.USER_STORY);
                case "2" -> editEpic();
                case "3" -> {
                    if (userController.isScrumMaster(projectController.getCurrentProjectId())) {
                        deleteEpic();
                    }
                }
                case "4" -> listEpicChatrooms();
                case "5" -> createEpicChatroom();
                case "0" -> running = false;
                default -> System.out.println("Invalid input.");
            }
        }
    }

    private void printEpicDetailsMenu() {
        System.out.println("\n===== Epic: " + projectController.getCurrentEpic().getTitle() + " =====");
        System.out.println("1. View user stories");
        System.out.println("2. Edit epic");
        System.out.println("3. Delete epic");
        System.out.println("4. View linked chatrooms");
        System.out.println("5. Create chatroom");
        System.out.println("0. Back");
    }

    private void createEpic() {
        String title;

        System.out.print("Enter name for your epic ");
        title = scanner.nextLine();

        projectController.createEpic(title);
    }

    private void listEpics() {
        ArrayList<String> epics = projectController.listEpics();
        for (String epic : epics) {
            System.out.println(epic);
        }
    }

    private void editEpic() {
        String title;
        System.out.print("Enter the new title for the epic you want to edit: ");
        title = scanner.nextLine().trim();

        if (title.isEmpty()) {
            System.out.println("Title cannot be empty. Please try again.");
            return;
        }

        System.out.println("Editing epic with new title: " + title);
        projectController.editEpic(title);
    }

    private void deleteEpic() {
        if (!userController.isScrumMaster(projectController.getCurrentProjectId())) {
            System.out.println("Only Scrum Masters can delete epics.");
        }

        Epic epic = projectController.getCurrentEpic();

        if (epic == null) {
            System.out.println("No epic selected.");
        }

        assert epic != null;
        epic.delete();
    }

    private void listEpicChatrooms() {
        ChatController chatController = screenManager.getChatController();
        List<Chatroom> chatrooms = chatController.getChatroomsForEpic(projectController.getCurrentEpic().getId());

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

    private void createEpicChatroom() {
        ChatController chatController = screenManager.getChatController();
        System.out.println("What is the name of the Chatroom: ");
        String name = scanner.nextLine();

        Chatroom chatroom = new Chatroom(name);
        chatroom.setLinkType(ChatroomLinkType.EPIC);
        chatroom.setLinkedEntityId(projectController.getCurrentEpic().getId());
        chatController.createChatroom(chatroom);
    }
}
