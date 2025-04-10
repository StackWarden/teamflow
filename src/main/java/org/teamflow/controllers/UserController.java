package org.teamflow.controllers;

import org.teamflow.database.DatabaseConnection;
import org.teamflow.models.User;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

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
                PreparedStatement insertStmt = conn.prepareStatement(insertSQL)
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

    public void deleteUser() {
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

    public List<User> getAllUsers() {
        List<User> users = new ArrayList<>();
        String sql = "SELECT * FROM user";

        try (
                Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql);
                ResultSet rs = stmt.executeQuery()
        ) {
            while (rs.next()) {
                User user = new User();
                user.setId(rs.getInt("id"));
                user.setUsername(rs.getString("username"));
                users.add(user);
            }
        } catch (SQLException e) {
            System.out.println("Error fetching users: " + e.getMessage());
        }

        return users;
    }

    public boolean addUserToProject(int userId, int projectId) {
        int roleId = 1;
        String checkSql = "SELECT * FROM user_project WHERE user_id = ? AND project_id = ? AND role_id = ?";
        String insertSql = "INSERT INTO user_project (user_id, project_id, role_id) VALUES (?, ?, ?)";

        try (
                Connection conn = DatabaseConnection.getConnection();
                PreparedStatement checkStmt = conn.prepareStatement(checkSql)
        ) {
            checkStmt.setInt(1, userId);
            checkStmt.setInt(2, projectId);
            checkStmt.setInt(3, roleId);
            ResultSet rs = checkStmt.executeQuery();

            if (rs.next()) {
                return false;
            }

            try (PreparedStatement insertStmt = conn.prepareStatement(insertSql)) {
                insertStmt.setInt(1, userId);
                insertStmt.setInt(2, projectId);
                insertStmt.setInt(3, roleId);
                insertStmt.executeUpdate();
                return true;
            }
        } catch (SQLException e) {
            System.out.println("Failed to add user to project: " + e.getMessage());
            return false;
        }
    }
}
