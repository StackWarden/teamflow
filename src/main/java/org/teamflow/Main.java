package org.teamflow;

import org.teamflow.controllers.ProjectController;
import org.teamflow.database.DatabaseConnection;
import org.teamflow.enums.ScreenType;
import org.teamflow.models.Project;

public class Main {
    public static void main(String[] args) {
        DatabaseConnection.checkConnection();
        ScreenManager screenManager = new ScreenManager();
        screenManager.switchTo(ScreenType.LOGIN);
    }
}