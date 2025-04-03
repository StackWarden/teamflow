package org.teamflow.controllers;

import org.teamflow.database.DatabaseConnection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class ProjectController {
    public int createProject(String name, String description) {
        String sql = "INSERT INTO Project (name, description) VALUES (?, ?)";

        try (PreparedStatement stmt = DatabaseConnection.getConnection().prepareStatement(sql)) {
            stmt.setString(1, name);
            stmt.setString(2, description);
            stmt.executeUpdate();
            return 1;
        } catch (SQLException e) {
            if (e.getErrorCode() == 1062) {
                return 2;
            } else {
                System.out.println(e.getMessage());
            }
            return 0;
        }
    }
}
