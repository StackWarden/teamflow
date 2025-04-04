package org.teamflow;

import org.teamflow.database.DatabaseConnection;
import org.teamflow.enums.ScreenType;

public class Main {
    public static void main(String[] args) {
        DatabaseConnection.checkConnection();

        ScreenManager screenManager = new ScreenManager();
        screenManager.switchTo(ScreenType.LOGIN);
    }
}