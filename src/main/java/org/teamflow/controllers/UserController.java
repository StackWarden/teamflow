package org.teamflow.controllers;

import org.teamflow.FileUtil;
import org.teamflow.database.DatabaseConnection;
import org.teamflow.models.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserController {
    private User loggedUser = null;
    private boolean isLoggedIn = false;

    public boolean isLoggedIn() {
        return isLoggedIn;
    }

    public void setLoggedIn(boolean loggedIn) {
        isLoggedIn = loggedIn;
    }

    public User getLoggedUser() {
        return loggedUser;
    }

    public int loginUser(String username) {
        String sql = "SELECT * FROM user WHERE username = ?";

        try (
            Connection conn = DatabaseConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)
        ) {
            ps.setString(1, username);
            ResultSet rs = ps.executeQuery();
            User user = createUserObject(rs);
            return (user != null) ? 1 : 2;

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public int registerUser(String username) {
        String insertSQL = "INSERT INTO user (username) VALUES (?)";
        String selectSQL = "SELECT * FROM user WHERE username = ?";

        try (
                Connection conn = DatabaseConnection.getConnection();
                PreparedStatement insertStmt = conn.prepareStatement(insertSQL);
        ) {
            insertStmt.setString(1, username);
            insertStmt.executeUpdate();

            try (PreparedStatement selectStmt = conn.prepareStatement(selectSQL)) {
                selectStmt.setString(1, username);
                ResultSet rs = selectStmt.executeQuery();
                createUserObject(rs);
            }
            return 1;
        } catch (SQLException e) {
            if (e.getErrorCode() == 1062) {
                return 2;
            } else {
                System.out.println("Failed to register user: " + e.getMessage());
                return 0;
            }
        }
    }

    private User createUserObject(ResultSet rs) throws SQLException {
        if (!rs.next()) {
            return null;
        }

        User user = new User();
        user.setId(rs.getInt("id"));
        user.setUsername(rs.getString("username"));
        loggedUser = user;
        setLoggedIn(true);

        return user;
    }
}
