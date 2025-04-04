package org.teamflow.controller;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.teamflow.controllers.ProjectController;
import org.teamflow.controllers.UserController;
import org.teamflow.database.DatabaseConnection;
import org.teamflow.models.ProjectCreationResult;
import org.teamflow.services.UserProjectRoleService;
import java.sql.Connection;
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
}
