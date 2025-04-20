package org.teamflow.controller;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.teamflow.controllers.ProjectController;
import org.teamflow.controllers.UserController;
import org.teamflow.database.DatabaseConnection;
import org.teamflow.models.Epic;
import org.teamflow.models.Project;
import org.teamflow.models.ProjectCreationResult;
import org.teamflow.models.UserStory;
import org.teamflow.services.UserProjectRoleService;

import java.sql.*;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class ProjectControllerTest {
    private static ProjectController controller;
    private static UserController userController;

    @BeforeAll
    public static void setup() {
        controller = new ProjectController();
        userController = new UserController();

        userController.registerUser("TestUser");
        userController.loginUser("TestUser");

        try {
            Connection conn = DatabaseConnection.getConnection();
            Statement stmt = conn.createStatement();
            stmt.executeUpdate("DELETE FROM Project WHERE name IN ('testproject', 'testprojectUserLogin', 'DeleteTestProject')");
            stmt.executeUpdate("DELETE FROM User_Project WHERE user_id = (SELECT id FROM User WHERE username = 'TestUser')");
            stmt.close();
        } catch (Exception e) {
            throw new RuntimeException("Failed to clean database before tests: " + e.getMessage(), e);
        }
    }

    @AfterAll
    public static void tearDown() {
        try {
            Connection conn = DatabaseConnection.getConnection();
            Statement stmt = conn.createStatement();
            stmt.executeUpdate("DELETE FROM Project WHERE name IN ('testproject', 'testprojectUserLogin', 'DeleteTestProject')");
            stmt.executeUpdate("DELETE FROM User_Project WHERE user_id = (SELECT id FROM User WHERE username = 'TestUser')");
            stmt.close();
        } catch (Exception e) {
            throw new RuntimeException("Failed to clean database after tests: " + e.getMessage(), e);
        }
    }


    @BeforeEach
    public void cleanDatabase() {
        try {
            Connection conn = DatabaseConnection.getConnection();
            Statement stmt = conn.createStatement();
            stmt.executeUpdate("DELETE FROM Project WHERE name = 'testproject' AND description = 'testdescription'");
            stmt.close();
        } catch (Exception e) {
            throw new RuntimeException("Failed to clean database: " + e.getMessage(), e);
        }
    }

    @Test
    public void testCreateProject_Returns1() {
        ProjectCreationResult result = controller.createProject("testproject", "testdescription");
        assertEquals(1, result.getStatus(), "Should return 1 for successful creation");
    }

    @Test
    public void testGetProjectNameAndUserRole() {
        ProjectCreationResult result = controller.createProject("testprojectUserLogin", "testdescription");
        UserProjectRoleService.assignRoleToUser(userController.getUserId(), result.getProject().getId(), "Scrum Master");

        assertEquals("Scrum Master", UserProjectRoleService.getUserRoleForProject(userController.getUserId(), result.getProject().getId()));
    }

    @Test
    public void testDeleteProjectWhenUserIsScrumMaster() {
        int userId = userController.getUserId();

        ProjectCreationResult result = controller.createProject("DeleteTestProject", "To be deleted");
        assertEquals(1, result.getStatus());

        Project project = result.getProject();
        int projectId = project.getId();

        UserProjectRoleService.assignRoleToUser(userId, project.getId(), "Scrum Master");

        assertTrue(UserProjectRoleService.isScrumMaster(userId, project.getId()), "User should be Scrum Master");

        controller.deleteProject();

        boolean exists = projectExists(projectId);
        assertFalse(exists, "Project should be deleted from database");
    }

    private boolean projectExists(int projectId) {
        String sql = "SELECT id FROM Project WHERE id = ?";
        try (
                Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)
        ) {
            stmt.setInt(1, projectId);
            return stmt.executeQuery().next();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void testCreateEditListAndDeleteUserStory() {
        String initialDescription = "testdescription";
        String updatedDescription = "updateddescription";
        int epicId = 1;
        int storyId;

        String insertSql = "INSERT INTO UserStory (epic_id, description) VALUES (?, ?)";
        try (
                Connection conn = DatabaseConnection.getConnection();
                PreparedStatement insertStmt = conn.prepareStatement(insertSql, Statement.RETURN_GENERATED_KEYS)
        ) {
            insertStmt.setInt(1, epicId);
            insertStmt.setString(2, initialDescription);
            insertStmt.executeUpdate();

            ResultSet keys = insertStmt.getGeneratedKeys();
            if (keys.next()) {
                storyId = keys.getInt(1);

                String updateSql = "UPDATE UserStory SET description = ? WHERE epic_id = ? AND id = ?";
                try (PreparedStatement updateStmt = conn.prepareStatement(updateSql)) {
                    updateStmt.setString(1, updatedDescription);
                    updateStmt.setInt(2, epicId);
                    updateStmt.setInt(3, storyId);
                    updateStmt.executeUpdate();
                }

                String selectSql = "SELECT description FROM UserStory WHERE id = ?";
                try (PreparedStatement selectStmt = conn.prepareStatement(selectSql)) {
                    selectStmt.setInt(1, storyId);
                    ResultSet rs = selectStmt.executeQuery();
                    assertTrue(rs.next(), "User story should exist");
                    assertEquals(updatedDescription, rs.getString("description"), "Description should be updated");
                }

                String deleteSql = "DELETE FROM UserStory WHERE id = ?";
                try (PreparedStatement deleteStmt = conn.prepareStatement(deleteSql)) {
                    deleteStmt.setInt(1, storyId);
                    deleteStmt.executeUpdate();
                    System.out.println("Created, edited, verified, and deleted UserStory with ID: " + storyId);
                }
            }

        } catch (SQLException e) {
            fail("SQL Exception: " + e.getMessage());
        }
    }

    @Test
    void testCreateEditDeleteProject() {
        String initialName = "Test Project";
        String initialDescription = "Test Desc";
        String updatedName = "Updated Project";
        String updatedDescription = "Updated Desc";

        // Create Project
        var result = controller.createProject(initialName, initialDescription);
        assertEquals(1, result.getStatus());
        Project created = result.getProject();
        assertNotNull(created);
        assertEquals(initialName, created.getName());

        int createdId = created.getId();

        // Edit Project
        boolean updated = controller.editProject(createdId, updatedName, updatedDescription);
        assertTrue(updated);

        Project fetched = controller.getProjectById(createdId);
        assertNotNull(fetched);
        assertEquals(updatedName, fetched.getName());

        // Delete Project
        controller.setCurrentProject(createdId);
        controller.deleteProject();

        Project deleted = controller.getProjectById(createdId);
        assertNull(deleted);
    }

    @Test
    void testCreateEditListAndDeleteUserStory_UsingController() {
        ProjectController controller = new ProjectController();

        // Setup
        int testProjectId = 1;
        int testEpicId = 1;
        String initialDescription = "testdescription";
        String updatedDescription = "updateddescription";

        assertTrue(controller.setCurrentProject(testProjectId), "Could not set current project");
        Epic epic = controller.getEpics().stream()
                .filter(e -> e.getId() == testEpicId)
                .findFirst()
                .orElseThrow(() -> new AssertionError("Epic not found"));

        controller.setCurrentEpic(epic);

        // Create
        controller.createUserStory(initialDescription);
        List<UserStory> stories = controller.getUserStories();
        UserStory createdStory = stories.stream()
                .filter(s -> s.getDescription().equals(initialDescription))
                .findFirst()
                .orElse(null);

        assertNotNull(createdStory, "Created user story should be in list");

        int storyId = createdStory.getId();

        // Edit
        controller.editUserStory(updatedDescription, storyId);
        UserStory updated = controller.getUserStories().stream()
                .filter(s -> s.getId() == storyId)
                .findFirst()
                .orElseThrow(() -> new AssertionError("Updated user story not found"));

        assertEquals(updatedDescription, updated.getDescription(), "Description should be updated");

        // Delete
        controller.deleteById("UserStory", storyId);
        List<UserStory> afterDelete = controller.getUserStories();
        boolean stillExists = afterDelete.stream().anyMatch(s -> s.getId() == storyId);
        assertFalse(stillExists, "User story should be deleted");

        System.out.println("Created, edited, verified, and deleted UserStory with ID: " + storyId);
    }

    @Test
    public void testUpdateEpicName_WithController() {
        ProjectController controller = new ProjectController();

        // Zorg dat je een bestaand project gebruikt voor de test
        int projectId = 1;
        assertTrue(controller.setCurrentProject(projectId), "Project should be set");

        String originalTitle = "Original Epic";
        String updatedTitle = "Updated Epic";

        // Maak een nieuwe epic aan
        controller.createEpic(originalTitle);

        // Haal de epic op
        Epic originalEpic = controller.getEpics().stream()
                .filter(e -> e.getTitle().equals(originalTitle))
                .findFirst()
                .orElse(null);

        assertNotNull(originalEpic, "Epic should have been created");

        // Zet als current epic
        controller.setCurrentEpic(originalEpic);

        // Update de titel via controller
        controller.editEpic(updatedTitle);

        // Haal opnieuw de epic lijst op
        Epic updatedEpic = controller.getEpics().stream()
                .filter(e -> e.getId() == originalEpic.getId())
                .findFirst()
                .orElse(null);

        assertNotNull(updatedEpic, "Updated epic should still exist");
        assertEquals(updatedTitle, updatedEpic.getTitle(), "Epic title should be updated");

        System.out.println("Epic updated from '" + originalTitle + "' to '" + updatedTitle + "'");
    }
}
