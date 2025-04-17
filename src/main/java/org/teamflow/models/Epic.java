package org.teamflow.models;

import org.teamflow.database.DatabaseConnection;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Set;

public class Epic {
    private int id;
    private int projectId;
    private String title;

    public Epic() {
    }

    public Epic(int id, int projectId, String title) {
        this.id = id;
        this.projectId = projectId;
        this.title = title;
    }

    public Epic(int projectId, String title) {
        this.projectId = projectId;
        this.title = title;
    }

    public int getId() {
        return id;
    }

    public int getProjectId() {
        return projectId;
    }

    public String getTitle() {
        return title;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void delete() {
        String sql = "DELETE FROM Epic WHERE id = ?";

        try (
                PreparedStatement stmt = DatabaseConnection.getConnection().prepareStatement(sql)
        ) {
            stmt.setInt(1, this.id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Failed to delete from epic: " + e.getMessage());
        }
    }

    public void setProjectId(int projectId) {
        this.projectId = projectId;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @Override
    public String toString() {
        return "Epic{" +
                "id=" + id +
                ", projectId=" + projectId +
                ", title='" + title + '\'' +
                '}';
    }
}
