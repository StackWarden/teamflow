package org.teamflow.models;

import org.teamflow.database.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class Project {
    private int id;
    private String name;
    private String description;

    public Project() {
        this.name = name;
        this.description = description;
    }

    public Project(int id, String name, String description) {
        this.id = id;
        this.name = name;
        this.description = description;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public static List<Project> getProjectsByUserId(int userId) {
        List<Project> projects = new ArrayList<>();

        String sql = """
        SELECT p.id, p.name, p.description
        FROM Project p
        INNER JOIN User_Project up ON p.id = up.project_id
        WHERE up.user_id = ?
    """;

        try (
                Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)
        ) {
            stmt.setInt(1, userId);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    projects.add(new Project(
                            rs.getInt("id"),
                            rs.getString("name"),
                            rs.getString("description")
                    ));
                }
            }

        } catch (SQLException e) {
            System.out.println("Failed to list user projects: " + e.getMessage());
        }

        return projects;
    }

    @Override
    public String toString() {
        return "Project{id=" + id + ", name='" + name + "', description='" + description + "'}";
    }

    public void deleteUserFromProject(int userId) {
        if (id <= 0) {
            System.out.println("Invalid project ID.");
            return;
        }

        String sql = "DELETE FROM User_Project WHERE user_id = ? AND project_id = ?";

        try (
                Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)
        ) {
            stmt.setInt(1, userId);
            stmt.setInt(2, this.id);

            int affected = stmt.executeUpdate();

            if (affected > 0) {
                System.out.println("User removed from project.");
            } else {
                System.out.println("User was not linked to this project.");
            }

        } catch (SQLException e) {
            System.out.println("Error while removing user from project: " + e.getMessage());
        }
    }

    public List<User> getMembers() {
        List<User> users = new ArrayList<>();

        if (id <= 0) {
            System.out.println("Invalid project ID.");
            return users;
        }

        String sql = """
            SELECT u.id, u.username
            FROM User_Project up
            JOIN User u ON u.id = up.user_id
            WHERE up.project_id = ?
        """;

        try (
                Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)
        ) {
            stmt.setInt(1, this.id);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    User user = new User();
                    user.setId(rs.getInt("id"));
                    user.setUsername(rs.getString("username"));
                    users.add(user);
                }
            }
        } catch (SQLException e) {
            System.out.println("Failed to fetch project members: " + e.getMessage());
        }

        return users;
    }

    public void setUserRole(int userId, Role role) {
        if (id <= 0 || userId <= 0 || role == null) {
            System.out.println("Invalid input for setting user role.");
            return;
        }

        String sql = """
            UPDATE User_Project
            SET role_id = ?
            WHERE user_id = ? AND project_id = ?
        """;

        try (
                Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)
        ) {
            stmt.setInt(1, role.getId());
            stmt.setInt(2, userId);
            stmt.setInt(3, this.id);

            int affected = stmt.executeUpdate();

            if (affected > 0) {
                System.out.println("User role updated successfully.");
            } else {
                System.out.println("No matching user found in this project.");
            }

        } catch (SQLException e) {
            System.out.println("Failed to update user role: " + e.getMessage());
        }
    }
}
