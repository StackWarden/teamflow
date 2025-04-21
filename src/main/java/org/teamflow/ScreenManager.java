package org.teamflow;

import org.teamflow.controllers.ChatController;
import org.teamflow.controllers.ProjectController;
import org.teamflow.controllers.UserController;
import org.teamflow.enums.ScreenType;
import org.teamflow.abstracts.Screen;
import org.teamflow.screens.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class ScreenManager {

    private final Map<ScreenType, Screen> screenRegistry = new HashMap<>();
    private Screen currentScreen;

    // Als je een nieuwe controller maak of iets anders dat overal gebruikt moet worden instantiate het dan hier.
    private final Scanner scanner = new Scanner(System.in);
    private final UserController userController = new UserController();
    private final ProjectController projectController = new ProjectController();
    private final ChatController chatController = new ChatController();

    public ScreenManager() {
        // Registreer alle Schermen die je aanmaakt (en wilt gebruiken) hieronder. Zie basetemplate voor meer info
        // Je moet ook bij ScreenType de titel van je scherm toevoegen dus als je UserStory scherm maak voeg die dan toe als USERSTORY
        // en dan hieronder register(ScreenType.USERSTORY, new UserStoryScreen(alle meuk));
        register(ScreenType.LOGIN, new LoginScreen(scanner, userController, this));
        register(ScreenType.DASHBOARD, new DashboardScreen(scanner, projectController, userController, this));
        register(ScreenType.PROJECT, new ProjectScreen(scanner, projectController, userController, this));
        register(ScreenType.PROJECT_MEMBERS, new ProjectMembersScreen(scanner, projectController, userController, this));
        register(ScreenType.EPIC, new EpicScreen(scanner, projectController, userController, this));
        register(ScreenType.USER_STORY, new UserStoryScreen(scanner, projectController, userController, this));
        register(ScreenType.TASK, new TaskScreen(scanner, projectController, userController, this));
        register(ScreenType.CHATROOM, new ChatroomScreen(scanner, chatController, userController, this));
    }

    private void register(ScreenType type, Screen screen) {
        screenRegistry.put(type, screen);
    }

    public ChatController getChatController() {
        return chatController;
    }

    public void switchTo(ScreenType type) {
        currentScreen = screenRegistry.get(type);
        if (currentScreen != null) {
            currentScreen.show();
        } else {
            System.out.println("Screen not found: " + type);
        }
    }

    public static void clearScreen() {
        try {
            String os = System.getProperty("os.name").toLowerCase();

            if (os.contains("windows")) {
                new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();
            } else {
                System.out.print("\033[H\033[2J");
                System.out.flush();
            }
        } catch (Exception e) {
            printBlankLines();
        }

        printBlankLines();
    }


    private static void printBlankLines() {
        for (int i = 0; i < 40; i++) {
            System.out.println();
        }
    }

    public UserController getUserController() {
        return userController;
    }

    public ProjectController getProjectController() {
        return projectController;
    }

    public Scanner getScanner() {
        return scanner;
    }
}
