package org.teamflow.controller;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.teamflow.controllers.ProjectController;
import org.teamflow.database.DatabaseConnection;
import org.teamflow.models.ProjectCreationResult;

import java.sql.Connection;
import java.sql.Statement;
import static org.junit.jupiter.api.Assertions.*;

public class ProjectControllerTest {

    private static ProjectController controller;

    @BeforeAll
    public static void setup() {
        controller = new ProjectController();
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
}
