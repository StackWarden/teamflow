package org.teamflow.controllers;

import org.teamflow.database.DatabaseConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserController {
    private boolean isLoggedIn = false;

    public boolean isLoggedIn() {
        return isLoggedIn;
    }

    public void setLoggedIn(boolean loggedIn) {
        isLoggedIn = loggedIn;
    }

    public int loginUser(String username) {
        String sql = "SELECT * FROM user WHERE username = ?";

        try (
            Connection conn = DatabaseConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)
        ) {
            ps.setString(1, username);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                setLoggedIn(true);
                return 1;
            } else {
                return 2;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public int registerUser(String username) {
        String sql = "INSERT INTO user (username) VALUES (?)";

        try (PreparedStatement stmt = DatabaseConnection.getConnection().prepareStatement(sql)) {
            stmt.setString(1, username);
            stmt.executeUpdate();
            isLoggedIn = true;
            return 1;
        } catch (SQLException e) {
            if (e.getErrorCode() == 1062) {
                return 2;
            } else {
                System.out.println("Failed to register user: " + e.getMessage());
            }
            return 0;
        }
    }
}
