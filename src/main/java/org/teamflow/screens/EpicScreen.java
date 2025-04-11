package org.teamflow.screens;

import org.teamflow.ScreenManager;
import org.teamflow.controllers.ProjectController;
import org.teamflow.controllers.UserController;
import org.teamflow.enums.ScreenType;
import org.teamflow.interfaces.Screen;
import org.teamflow.models.Epic;

import java.util.ArrayList;
import java.util.Scanner;

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
        System.out.println("[TODO] Toon lijst van epics met nummers om te selecteren]");
        // Kies epic → projectController.setCurrentEpic(epic);

        // Daarna:
        showEpicDetailsMenu(); // -> nieuw submenu
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
                    if (UserController.isScrumMaster()) {
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
        System.out.println("[TODO] Edit epic logic");
        // Bijvoorbeeld: wijzig titel, sla opnieuw op
    }

    private void deleteEpic() {
        if (!UserController.isScrumMaster()) {
            System.out.println("Only Scrum Masters can delete epics.");
        }

        Epic epic = projectController.getCurrentEpic();

        if (epic == null) {
            System.out.println("No epic selected.");
        }

        assert epic != null;
        projectController.deleteById("Epic", epic.getId());
    }

    private void listEpicChatrooms() {
        System.out.println("[TODO] List all chatrooms linked to this epic");
        // Je kunt hier later Epic_Chatroom gebruiken
    }

    private void createEpicChatroom() {
        System.out.println("[TODO] Create a new chatroom and link it to this epic");
        // ChatController.createChatroom(...) + koppelen via Epic_Chatroom
    }
}
