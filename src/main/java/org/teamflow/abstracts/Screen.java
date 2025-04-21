package org.teamflow.abstracts;

import org.teamflow.ScreenManager;
import org.teamflow.controllers.ChatController;
import org.teamflow.controllers.ProjectController;
import org.teamflow.controllers.UserController;

import java.util.Scanner;

public abstract class Screen {
    protected ScreenManager screenManager;
    protected ProjectController projectController;
    protected UserController userController;
    protected ChatController chatController;
    protected Scanner scanner;

    public Screen(ScreenManager screenManager) {
        this.screenManager = screenManager;
        this.projectController = screenManager.getProjectController();
        this.userController = screenManager.getUserController();
        this.chatController = screenManager.getChatController();
        this.scanner = screenManager.getScanner();
    }

    public abstract void show();

    protected void printBreadcrumb(String... parts) {
        System.out.println();
        System.out.print("📍 ");
        for (int i = 0; i < parts.length; i++) {
            System.out.print(parts[i]);
            if (i != parts.length - 1) System.out.print(" > ");
        }
        System.out.println();
        System.out.println("────────────────────────────────────────────");
    }
}
