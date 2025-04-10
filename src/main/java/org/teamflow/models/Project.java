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

    public Project(String name, String description) {
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
}
