package org.teamflow.controller;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.teamflow.controllers.UserController;
import org.teamflow.database.DatabaseConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import static org.junit.jupiter.api.Assertions.*;

public class UserControllerTest {

    private static UserController controller;

    @BeforeAll
    public static void setup() {
        controller = new UserController();
    }

    @BeforeEach
    public void cleanDatabase() {
        try {
            Connection conn = DatabaseConnection.getConnection();
            Statement stmt = conn.createStatement();
            stmt.executeUpdate("DELETE FROM user WHERE username = 'testuser'");
            stmt.close();
        } catch (Exception e) {
            throw new RuntimeException("Failed to clean database: " + e.getMessage(), e);
        }
    }

    @Test
    public void testLogin_Returns1() {
        int result = controller.registerUser("testuser");
        assertEquals(1, result, "Should return 1 for successful login");
        assertTrue(controller.isLoggedIn(), "user should be logged in");
    }

    @Test
    public void testRegisterNewUser_Returns1() {
        int result = controller.registerUser("testuser");
        assertEquals(1, result, "Should return 1 for successful registration");
        assertTrue(controller.isLoggedIn(), "User should be logged in after registration");
    }

    @Test
    public void testRegisterDuplicateUser_Returns2() {
        controller.registerUser("testuser"); // First registration
        int result = controller.registerUser("testuser"); // Attempt duplicate
        assertEquals(2, result, "Should return 2 for duplicate username");
    }

    @Test
    public void testIsLoggedInInitiallyFalse() {
        UserController fresh = new UserController();
        assertFalse(fresh.isLoggedIn(), "User should not be logged in before registration");
    }

    @Test
    public void testDeleteUserFromProject_DeletesUserAndLogsOut() {
        controller.registerUser("testuser");

        controller.deleteUser();

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement("SELECT * FROM user WHERE username = ?")) {

            stmt.setString(1, "testuser");
            ResultSet rs = stmt.executeQuery();

            assertFalse(rs.next(), "User should be deleted from database");

        } catch (Exception e) {
            fail("Database check failed: " + e.getMessage());
        }

        assertFalse(controller.isLoggedIn(), "User should be logged out after deletion");
    }
}
