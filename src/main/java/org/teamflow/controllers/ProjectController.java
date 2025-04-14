package org.teamflow.controllers;

import org.teamflow.database.DatabaseConnection;
import org.teamflow.models.*;
import org.teamflow.services.UserProjectRoleService;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class ProjectController {
    private Project currentProject = null;
    private Epic currentEpic = null;
    private UserStory currentUserStory = null;
    private Task currentTask = null;

    public void setCurrentTask(Task currentTask) {
        this.currentTask = currentTask;
    }
    public Task getCurrentTask() {
        return currentTask;
    }

    public void setCurrentEpic(Epic epic) {
        this.currentEpic = epic;
    }

    public UserStory getCurrentUserStory() {
        return currentUserStory;
    }

    public void setCurrentUserStory(UserStory userStory) {
        this.currentUserStory = userStory;
    }
    public Epic getCurrentEpic() {
        return currentEpic;
    }
    public boolean setCurrentProject(int currentProjectId) {
        Project project = getProjectById(currentProjectId);
        if(project != null) {
            currentProject = project;
            return true;
        }
        return false;
    }

    public Project getCurrentProject() {
        return currentProject;
    }
    public int getCurrentProjectId() { return currentProject.getId(); }
    public int getCurrentTaskId() { return currentTask.getId(); }

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

    public void removeUserFromProject(int userId) {
        currentProject.deleteUserFromProject(userId);
    }

    public void assignUserToTask(int userId) {
        currentTask.assignUserToTask(userId);
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

    public List<User> getProjectMembers(int projectId) {
        return currentProject.getMembers();
    }

    public List<Role> getAllRoles() {
        return Role.getAllRoles();
    }

    public void changeUserRoleInProject(int userId, Role role) {
        Project current = getCurrentProject();
        if (current == null) {
            System.out.println("No project selected.");
            return;
        }
        current.setUserRole(userId, role);
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

    public void createUserStory(String descriptionInput) {
        String sql = "INSERT INTO UserStory (epic_id, description) VALUES (?, ?)";
        try (
                PreparedStatement stmt = DatabaseConnection.getConnection().prepareStatement(sql)
        ) {
            stmt.setInt(1, currentEpic.getId());
            stmt.setString(2, descriptionInput);
            stmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Failed to create user story: " + e.getMessage());
        }
    }

    public void editUserStory(String descriptionInput, int storyId) {
        String sql = "UPDATE UserStory SET description = ? WHERE epic_id = ? AND id = ?";
        try (
                PreparedStatement stmt = DatabaseConnection.getConnection().prepareStatement(sql)
        ) {
            stmt.setString(1, descriptionInput);
            stmt.setInt(2, currentEpic.getId());
            stmt.setInt(3, storyId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Failed to edit user story: " + e.getMessage());
        }
    }

    public void deleteById(String tableName, int id) {
        String sql = "DELETE FROM " + tableName + " WHERE id = ?";

        if (!Set.of("Epic", "UserStory", "Task").contains(tableName)) {
            throw new IllegalArgumentException("Invalid table name: " + tableName);
        }

        try (
                PreparedStatement stmt = DatabaseConnection.getConnection().prepareStatement(sql)
        ) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Failed to delete from " + tableName + ": " + e.getMessage());
        }
    }

    public List<UserStory> getUserStories() {
        ArrayList<UserStory> stories = new ArrayList<>();
        String sql = "SELECT id, description FROM UserStory WHERE epic_id = ?";
        try (
                PreparedStatement stmt = DatabaseConnection.getConnection().prepareStatement(sql)
        ) {
            stmt.setInt(1, currentEpic.getId());
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                stories.add(new UserStory (rs.getInt("id"), currentEpic.getId() ,rs.getString("description")));
            }
        } catch (SQLException e) {
            System.out.println("Failed to list user stories: " + e.getMessage());
        }
        return stories;
    }

    public ArrayList<String> listUserStories() {
        ArrayList<String> userStories = new ArrayList<>();
        String sql = "SELECT id, description FROM UserStory WHERE epic_id = ?";
        try (
                PreparedStatement stmt = DatabaseConnection.getConnection().prepareStatement(sql)
        ) {
            stmt.setInt(1, currentEpic.getId());
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                userStories.add(rs.getInt("id") + ": " + rs.getString("description"));
            }
        } catch (SQLException e) {
            System.out.println("Failed to list user stories: " + e.getMessage());
        }
        return userStories;
    }

    public void createEpic (String title) {
        String sql = "INSERT INTO Epic (title, project_id) VALUES (?,?)";
        try (
                PreparedStatement stmt = DatabaseConnection.getConnection().prepareStatement(sql)
        ) {
            stmt.setString(1, title);
            stmt.setInt(2, currentProject.getId());
            stmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Failed to create epic: " + e.getMessage());
        }
    }

    public List<Epic> getEpics() {
        ArrayList<Epic> epics = new ArrayList<>();
        String sql = "SELECT id, title FROM Epic WHERE project_id = ?";
        try (
                PreparedStatement stmt = DatabaseConnection.getConnection().prepareStatement(sql)
        ) {
            stmt.setInt(1, currentProject.getId());
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                epics.add(new Epic (rs.getInt("id"), currentProject.getId() ,rs.getString("title")));
            }
        } catch (SQLException e) {
            System.out.println("Failed to list epics: " + e.getMessage());
        }
        return epics;
    }

    public ArrayList<String> listEpics() {
        ArrayList<String> epics = new ArrayList<>();
        String sql = "SELECT id, title FROM Epic WHERE project_id = ?";
        try (
                PreparedStatement stmt = DatabaseConnection.getConnection().prepareStatement(sql)
        ) {
            stmt.setInt(1, currentProject.getId());
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                epics.add(rs.getInt("id") + ": " + rs.getString("title"));
            }
        } catch (SQLException e) {
            System.out.println("Failed to list epics: " + e.getMessage());
        }
        return epics;
    }

    public void createTask(String title, String status) {
        String sql = "INSERT INTO Task (title, status, story_id) VALUES (?, ?, ?)";
        try (
                PreparedStatement stmt = DatabaseConnection.getConnection().prepareStatement(sql)
        ) {
            stmt.setString(1, title);
            stmt.setString(2, status);
            stmt.setInt(3, currentUserStory.getId());
            stmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Failed to create task: " + e.getMessage());
        }
    }

    public void editTask(int id, String status) {
        String sql = "UPDATE Task SET status = ? WHERE id = ?";
        try (
                PreparedStatement stmt = DatabaseConnection.getConnection().prepareStatement(sql)
        ) {
            stmt.setString(1, status);
            stmt.setInt(2, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Failed to edit task: " + e.getMessage());
        }
    }

    public ArrayList<String> listTasks() {
        ArrayList<String> tasks = new ArrayList<>();
        String sql = "SELECT id, title, status FROM Task WHERE story_id = ?";
        try (
                PreparedStatement stmt = DatabaseConnection.getConnection().prepareStatement(sql)
        ) {
            stmt.setInt(1, currentTask.getId());
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                tasks.add(rs.getInt("id") + ": " + rs.getString("title") + ": " + rs.getString("status"));
            }
        } catch (SQLException e) {
            System.out.println("Failed to list Tasks: " + e.getMessage());
        }
        return tasks;
    }

    public List<Task> getTasks() {
        ArrayList<Task> tasks = new ArrayList<>();
        String sql = "SELECT id, title, status FROM Task WHERE story_id = ?";
        try (
                PreparedStatement stmt = DatabaseConnection.getConnection().prepareStatement(sql)
        ) {
            stmt.setInt(1, currentUserStory.getId());
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                tasks.add(new Task (rs.getInt("id"), rs.getString("title"), rs.getString("status"), currentUserStory.getId()));
            }
        } catch (SQLException e) {
            System.out.println("Failed to list tasks: " + e.getMessage());
        }
        return tasks;
    }
}
