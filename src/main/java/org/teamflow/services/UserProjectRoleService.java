package org.teamflow.services;

import org.teamflow.database.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserProjectRoleService {

    public static boolean isScrumMaster(int userId, int projectId) {
        String sql = """
            SELECT r.role_name 
            FROM user_project up
            JOIN role r ON up.role_id = r.id
            WHERE up.user_id = ? AND up.project_id = ?
        """;

        try (
                Connection conn = DatabaseConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)
        ) {
            ps.setInt(1, userId);
            ps.setInt(2, projectId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return "Scrum Master".equalsIgnoreCase(rs.getString("role_name"));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return false;
    }

    public static void assignRoleToUser(int userId, int projectId, String roleName) {
        String findRole = "SELECT id FROM role WHERE role_name = ?";
        String insertLink = "INSERT INTO user_project (user_id, project_id, role_id) VALUES (?, ?, ?)";

        try (
                Connection conn = DatabaseConnection.getConnection();
                PreparedStatement roleStmt = conn.prepareStatement(findRole);
                PreparedStatement insertStmt = conn.prepareStatement(insertLink)
        ) {
            roleStmt.setString(1, roleName);
            ResultSet rs = roleStmt.executeQuery();

            if (!rs.next()) {
                throw new RuntimeException("Role not found: " + roleName);
            }

            int roleId = rs.getInt("id");
            insertStmt.setInt(1, userId);
            insertStmt.setInt(2, projectId);
            insertStmt.setInt(3, roleId);
            insertStmt.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Failed to assign role", e);
        }
    }

    public static String getUserRoleForProject(int userId, int projectId) {
        String sql = """
        SELECT r.role_name
        FROM user_project up
        JOIN role r ON up.role_id = r.id
        WHERE up.user_id = ? AND up.project_id = ?
    """;

        try (
                Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)
        ) {
            stmt.setInt(1, userId);
            stmt.setInt(2, projectId);

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getString("role_name");
            } else {
                return null;
            }

        } catch (SQLException e) {
            System.out.println("Error retrieving user role: " + e.getMessage());
            return null;
        }
    }
}