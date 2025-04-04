package org.teamflow.controllers;

import org.teamflow.database.DatabaseConnection;
import org.teamflow.models.Project;
import org.teamflow.models.ProjectCreationResult;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Scanner;

public class ProjectController {
    Scanner scanner = new Scanner(System.in);

    public ProjectCreationResult createProject(String name, String description) {
        String sql = "INSERT INTO Project (name, description) VALUES (?, ?)";

        try (PreparedStatement stmt = DatabaseConnection.getConnection().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, name);
            stmt.setString(2, description);
            stmt.executeUpdate();
            ResultSet keys = stmt.getGeneratedKeys();
            if (keys.next()) {
                int projectId = keys.getInt(1);
                Project project = new Project(projectId, name, description);
                return new ProjectCreationResult(1, project);
            } else {
                return new ProjectCreationResult(0, null);
            }
        } catch (SQLException e) {
            if (e.getErrorCode() == 1062) {
                return new ProjectCreationResult(2, null);
            } else {
                System.out.println("Failed to create project: " + e.getMessage());
                return new ProjectCreationResult(0, null);
            }
        }
    }

    public void removeUserFromProjectByName() {
        String getUserIdSql = "SELECT id FROM user WHERE username = ?";
        String getProjectIdSql = "SELECT id FROM project WHERE name = ?";
        String deleteLinkSql = "DELETE FROM User_Project WHERE user_id = ? AND project_id = ?";

        try (
                PreparedStatement userStmt = DatabaseConnection.getConnection().prepareStatement(getUserIdSql);
                PreparedStatement projectStmt = DatabaseConnection.getConnection().prepareStatement(getProjectIdSql);
                PreparedStatement deleteStmt = DatabaseConnection.getConnection().prepareStatement(deleteLinkSql)
        ) {
            System.out.println("Enter username: ");
            String username = scanner.nextLine();
            System.out.println("Enter project name: ");
            String projectName  = scanner.nextLine();

            // Get user ID
            userStmt.setString(1, username);
            ResultSet userRs = userStmt.executeQuery();
            if (!userRs.next()) {
                System.out.println("User not found: " + username);
                return;
            }
            int userId = userRs.getInt("id");

            // Get project ID
            projectStmt.setString(1, projectName);
            ResultSet projectRs = projectStmt.executeQuery();
            if (!projectRs.next()) {
                System.out.println("Project not found: " + projectName);
                return;
            }
            int projectId = projectRs.getInt("id");

            // Delete the link
            deleteStmt.setInt(1, userId);
            deleteStmt.setInt(2, projectId);
            int affectedRows = deleteStmt.executeUpdate();

            if (affectedRows > 0) {
                System.out.println("User removed from project.");
            } else {
                System.out.println("User was not assigned to the project.");
            }

        } catch (SQLException e) {
            System.out.println("Failed to remove user from project: " + e.getMessage());
        }
    }

    public void editProjectByName() {
        System.out.print("Enter the name of the project to edit: ");
        String oldName = scanner.nextLine();

        String selectSql = "SELECT id, name, description FROM project WHERE name = ?";
        String updateSql = "UPDATE project SET name = ?, description = ? WHERE id = ?";

        try (
                PreparedStatement selectStmt = DatabaseConnection.getConnection().prepareStatement(selectSql);
                PreparedStatement updateStmt = DatabaseConnection.getConnection().prepareStatement(updateSql)
        ) {
            // Get current project
            selectStmt.setString(1, oldName);
            ResultSet rs = selectStmt.executeQuery();

            if (!rs.next()) {
                System.out.println("Project not found: " + oldName);
                return;
            }

            int projectId = rs.getInt("id");
            String currentName = rs.getString("name");
            String currentDescription = rs.getString("description");

            System.out.println("Current Project Name: " + currentName);
            System.out.println("Current Description: " + currentDescription);

            // Ask for new values
            System.out.print("Enter new project name (leave blank to keep current): ");
            String newName = scanner.nextLine();
            if (newName.isBlank()) newName = currentName;

            System.out.print("Enter new project description (leave blank to keep current): ");
            String newDescription = scanner.nextLine();
            if (newDescription.isBlank()) newDescription = currentDescription;

            // Update project
            updateStmt.setString(1, newName);
            updateStmt.setString(2, newDescription);
            updateStmt.setInt(3, projectId);

            int rows = updateStmt.executeUpdate();
            if (rows > 0) {
                System.out.println("Project updated successfully.");
            } else {
                System.out.println("Failed to update project.");
            }
        } catch (SQLException e) {
            System.out.println("Error editing project: " + e.getMessage());
        }
    }
}
