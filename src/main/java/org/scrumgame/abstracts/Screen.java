package org.scrumgame.abstracts;

import org.scrumgame.ScreenManager;

import java.util.Scanner;

public abstract class Screen {
    protected Scanner scanner;

    public ScreenManager screenManager;
    public String alertMessage = null;

    public Screen(ScreenManager screenManager) {
        this.screenManager = screenManager;
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
        printAlertMessage();
        System.out.println("────────────────────────────────────────────");
    }

    public void setAlertMessage(String errorMessage) {
        this.alertMessage = errorMessage;
    }

    public void printAlertMessage() {
        if (alertMessage != null) {
            System.out.println("Alert: " + alertMessage);
            alertMessage = null;
        }
    }
}
