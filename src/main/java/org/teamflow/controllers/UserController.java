package org.teamflow.controllers;

import org.teamflow.database.DatabaseConnection;
import org.teamflow.models.User;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserController {
    private User currentUser = null;
    private boolean isLoggedIn = false;

    public boolean isLoggedIn() {
        return isLoggedIn;
    }

    public void setLoggedIn(boolean loggedIn) {
        isLoggedIn = loggedIn;
    }

    public User getLoggedUser() { return currentUser;}

    public int getUserId() { return currentUser.getId();}

    public String getUsername() { return currentUser.getUsername();}

    public void logout() {
        isLoggedIn = false;
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
    public void removeUserFromProject() {
        String sql = "DELETE FROM user WHERE username = ?";

        if (!isLoggedIn || getUsername() == null) {
            return;
        }

        try (PreparedStatement stmt = DatabaseConnection.getConnection().prepareStatement(sql)) {
            stmt.setString(1, getUsername());
            stmt.executeUpdate();
            setLoggedIn(false);
            logout();
        } catch (SQLException e) {
            System.out.println("Failed to delete user: " + e.getMessage());
        }
    }
    private User createUserObject(ResultSet rs) throws SQLException {
        if (!rs.next()) {
            return null;
        }

        User user = new User();
        user.setId(rs.getInt("id"));
        user.setUsername(rs.getString("username"));
        currentUser = user;
        setLoggedIn(true);

        return user;
    }
}
