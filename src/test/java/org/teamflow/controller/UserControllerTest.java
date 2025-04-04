package org.teamflow.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.teamflow.controllers.UserController;
import org.teamflow.database.DatabaseConnection;
import org.teamflow.models.User;
import java.sql.Connection;
import java.sql.Statement;
import static org.junit.jupiter.api.Assertions.*;

public class UserControllerTest {

    private static UserController controller;

    @BeforeEach
    public void setup() {
        controller = new UserController();
    }

    @BeforeEach
    public void cleanDatabase() {
        try {
            Connection conn = DatabaseConnection.getConnection();
            Statement stmt = conn.createStatement();
            stmt.executeUpdate("DELETE FROM user WHERE username = 'testuser'");
            stmt.executeUpdate("DELETE FROM user WHERE username = 'anotheruser'");
            stmt.close();
        } catch (Exception e) {
            throw new RuntimeException("Failed to clean database: " + e.getMessage(), e);
        }
    }

    @Test
    public void testRegisterNewUser_Returns1() {
        int result = controller.registerUser("testuser");
        assertEquals(1, result, "Should return 1 for successful registration");
        assertTrue(controller.isLoggedIn(), "User should be logged in after registration");

        User logged = controller.getLoggedUser();
        assertNotNull(logged, "Logged-in user should not be null");
        assertEquals("testuser", logged.getUsername(), "Username should match registered name");
        assertTrue(logged.getId() > 0, "User ID should be greater than 0");
    }

    @Test
    public void testRegisterDuplicateUser_Returns2() {
        controller.registerUser("testuser"); // First registration
        int result = controller.registerUser("testuser"); // Attempt duplicate
        assertEquals(2, result, "Should return 2 for duplicate username");
    }

    @Test
    public void testLoginWithUnregisteredUser_Returns2() {
        int result = controller.loginUser("nonexistent");
        assertEquals(2, result, "Should return 2 for user not found");
        assertFalse(controller.isLoggedIn(), "User should not be logged in");
        assertNull(controller.getLoggedUser(), "No user should be stored in loggedUser");
    }

    @Test
    public void testLoginWithRegisteredUser_Returns1() {
        controller.registerUser("testuser");

        UserController fresh = new UserController();
        int result = fresh.loginUser("testuser");

        assertEquals(1, result, "Should return 1 for successful login");
        assertTrue(fresh.isLoggedIn(), "User should be logged in");
        assertNotNull(fresh.getLoggedUser(), "Logged user should not be null");
        assertEquals("testuser", fresh.getLoggedUser().getUsername(), "Username should match");
    }

    @Test
    public void testIsLoggedInInitiallyFalse() {
        UserController fresh = new UserController();
        assertFalse(fresh.isLoggedIn(), "User should not be logged in before registration");
        assertNull(fresh.getLoggedUser(), "Logged user should be null before login");
    }

    @Test
    public void testRegisterAndLoginFlowWorksCorrectly() {
        UserController fresh = new UserController();

        int registerResult = fresh.registerUser("anotheruser");
        assertEquals(1, registerResult);

        User logged = fresh.getLoggedUser();
        assertNotNull(logged);
        assertEquals("anotheruser", logged.getUsername());
    }
}
