package org.teamflow.controllers;

import org.teamflow.database.DatabaseConnection;
import org.teamflow.models.Project;
import org.teamflow.models.ProjectCreationResult;
import org.teamflow.models.User;
import org.teamflow.services.UserProjectRoleService;
import java.sql.*;
import java.util.ArrayList;

public class ProjectController {
    private Project currentProject = null;

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
            System.out.println("Enter project name: ");

            userStmt.setString(1, username);
            ResultSet userRs = userStmt.executeQuery();
            if (!userRs.next()) {
                System.out.println("User not found: " + username);
                return false;
            }
            int userId = userRs.getInt("id");

            projectStmt.setString(1, projectName);
            ResultSet projectRs = projectStmt.executeQuery();
            if (!projectRs.next()) {
                System.out.println("Project not found: " + projectName);
                return false;
            }
            int projectId = projectRs.getInt("id");

            deleteStmt.setInt(1, userId);
            deleteStmt.setInt(2, projectId);
            int affectedRows = deleteStmt.executeUpdate();

            return affectedRows > 0;

        } catch (SQLException e) {
            System.out.println("Failed to remove user from project: " + e.getMessage());
            return false;
        }
    }

    public boolean editProject(int projectId, String newName, String newDescription) {
        String sql = "UPDATE project SET name = ?, description = ? WHERE id = ?";
        try (
                PreparedStatement stmt = DatabaseConnection.getConnection().prepareStatement(sql)
        ) {
            stmt.setString(1, newName);
            stmt.setString(2, newDescription);
            stmt.setInt(3, projectId);

            int rows = stmt.executeUpdate();
            return rows > 0;

        } catch (SQLException e) {
            System.out.println("Failed to edit project: " + e.getMessage());
            return false;
        }
    }

    public Project getProjectById(int id) {
        String sql = "SELECT id, name, description FROM project WHERE id = ?";
        try (
                PreparedStatement stmt = DatabaseConnection.getConnection().prepareStatement(sql)
        ) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return new Project(rs.getInt("id"), rs.getString("name"), rs.getString("description"));
            }
        } catch (SQLException e) {
            System.out.println("Failed to get project: " + e.getMessage());
        }
        return null;
    }

    public ArrayList<Project> listProjects() {
        String sql = "SELECT id, name, description FROM project";
        return getProjects(sql);
    }

    public ArrayList<Project> listProjectsWhereScrummaster(int uid) {
        int scrumMasterRoleId = 3;

        String sql = "SELECT p.id, p.name, p.description " +
                "FROM project p " +
                "JOIN User_project up ON p.id = up.project_id " +
                "WHERE up.user_id = ? AND up.role_id = ?";

        return getProjects(sql, uid, scrumMasterRoleId);
    }

    private ArrayList<Project> getProjects(String sql, Object... params) {
        ArrayList<Project> projects = new ArrayList<>();
        try (
                Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)
        ) {
            for (int i = 0; i < params.length; i++) {
                stmt.setObject(i + 1, params[i]);
            }

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    projects.add(new Project(
                            rs.getInt("id"),
                            rs.getString("name"),
                            rs.getString("description")
                    ));
                }
            }
            return projects;
        } catch (SQLException e) {
            System.out.println("Failed to list projects: " + e.getMessage());
        }
        return projects;
    }
}
