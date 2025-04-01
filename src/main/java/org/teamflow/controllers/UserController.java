package org.teamflow.controllers;

import org.teamflow.FileUtil;
import org.teamflow.database.DatabaseConnection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class UserController {
    private boolean isLoggedIn = false;
    public boolean isLoggedIn() {
        return isLoggedIn;
    }
    public void setLoggedIn(boolean loggedIn) {
        isLoggedIn = loggedIn;
    }

    public UserController() {
        String sql = FileUtil.readSQLFile("src/main/java/org/teamflow/database/tables/user.sql");

        try {
            DatabaseConnection.query(sql);
            System.out.println("User table ensured.");
        } catch (SQLException e) {
            throw new RuntimeException("Failed to create user table", e);
        }
    }

    public int registerUser(String username) {
        String sql = "INSERT INTO user (username) VALUES (?)";

        try (PreparedStatement stmt = DatabaseConnection.getConnection().prepareStatement(sql)) {
            stmt.setString(1, username);
            stmt.executeUpdate();
            isLoggedIn = true;
            return 1;
        } catch (SQLException e) {
            if (e.getErrorCode() == 1062) {
                return 2;
            } else {
                System.out.println("Failed to register user: " + e.getMessage());
            }
            return 0;
        }
    }
}
