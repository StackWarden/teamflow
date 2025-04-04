package org.teamflow.controller;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.teamflow.controllers.ProjectController;
import org.teamflow.controllers.UserController;
import org.teamflow.database.DatabaseConnection;
import org.teamflow.models.Project;
import org.teamflow.models.ProjectCreationResult;
import org.teamflow.services.UserProjectRoleService;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Statement;
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
}
