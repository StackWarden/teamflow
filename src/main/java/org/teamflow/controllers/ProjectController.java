package org.teamflow.controllers;

import org.teamflow.database.DatabaseConnection;
import org.teamflow.models.Project;
import org.teamflow.models.ProjectCreationResult;
import org.teamflow.models.User;
import org.teamflow.services.UserProjectRoleService;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Scanner;

public class ProjectController {
    private Project currentProject = null;
    Scanner scanner = new Scanner(System.in);

    public int getCurrentProjectId() { return currentProject.getId();}

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
                currentProject = project;
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
    public Project getCurrentProject() {
        return currentProject;
    }

    public String getCurrentProjectName() {
        return currentProject.getName();
    }

    public String getProjectNameAndUserRole(User user) {
        String userRole = UserProjectRoleService.getUserRoleForProject(user.getId(), getCurrentProjectId());
        return "Project: " + getCurrentProjectName() + " (" + userRole + ")";
    }

    public void deleteProject() {
        String sql = "DELETE FROM Project WHERE id = ?";
        if (currentProject == null) {
            return;
        }
        try (PreparedStatement stmt = DatabaseConnection.getConnection().prepareStatement(sql)) {
            stmt.setInt(1, getCurrentProjectId());
            stmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Failed to delete user: " + e.getMessage());
        }
    }

    public boolean removeUserFromProjectByName(String username, String projectName) {
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
                return false;
            }
            int userId = userRs.getInt("id");

            // Get project ID
            projectStmt.setString(1, projectName);
            ResultSet projectRs = projectStmt.executeQuery();
            if (!projectRs.next()) {
                System.out.println("Project not found: " + projectName);
                return false;
            }
            int projectId = projectRs.getInt("id");

            // Delete the link
            deleteStmt.setInt(1, userId);
            deleteStmt.setInt(2, projectId);
            int affectedRows = deleteStmt.executeUpdate();

            return affectedRows > 0;

        } catch (SQLException e) {
            System.out.println("Failed to remove user from project: " + e.getMessage());
            return false;
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
    public ArrayList<Project> listProjects() {
        String sql = "SELECT id, name, description FROM project";
        ArrayList<Project> projects = new ArrayList<Project>();
        try (
                PreparedStatement stmt = DatabaseConnection.getConnection().prepareStatement(sql);
                ResultSet rs = stmt.executeQuery()
        ) {
            while (rs.next()) {
                projects.add(new Project(rs.getInt("id"), rs.getString("name"), rs.getString("description")));
            }
            return projects;
        } catch (SQLException e) {
            System.out.println("Failed to list projects: " + e.getMessage());
            return projects;
        }
    }
}
