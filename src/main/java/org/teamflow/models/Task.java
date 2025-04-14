package org.teamflow.models;

import org.teamflow.database.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class Task {
    private int id;
    private String title;
    private String status;
    private int storyId;

    public Task() {
    }

    public Task(int id, String title, String status, int storyId) {
        this.id = id;
        this.title = title;
        this.status = status;
        this.storyId = storyId;
    }

    public Task(String title, String status, int storyId) {
        this.title = title;
        this.status = status;
        this.storyId = storyId;
    }

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getStatus() {
        return status;
    }

    public int getStoryId() {
        return storyId;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setStoryId(int storyId) {
        this.storyId = storyId;
    }

    @Override
    public String toString() {
        return "Task{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", status='" + status + '\'' +
                ", storyId=" + storyId +
                '}';
    }

    public void assignUserToTask(int userId) {
        if (id <= 0) {
            System.out.println("Invalid task ID.");
            return;
        }

        String sql = "INSERT INTO User_Task (task_id, user_id) VALUES (?, ?)";
        try {
            PreparedStatement stmt = DatabaseConnection.getConnection().prepareStatement(sql);
            stmt.setInt(1, id);
            stmt.setInt(2, userId);
            stmt.execute();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
    public void assignedUsers(int userId) {
        if (id <= 0) {
            System.out.println("Invalid task ID.");
            return;
        }

        String sql = "SELECT FROM User_Task (task_id, user_id) VALUES (?, ?)";
        try {
            PreparedStatement stmt = DatabaseConnection.getConnection().prepareStatement(sql);
            stmt.setInt(1, id);
            stmt.setInt(2, userId);
            stmt.execute();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public void deleteUserFromTask(int userId) {
        if (id <= 0) {
            System.out.println("Invalid task ID.");
            return;
        }

        String sql = "DELETE FROM User_Task WHERE user_id = ? AND task_id = ?";

        try (
                Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)
        ) {
            stmt.setInt(1, userId);
            stmt.setInt(2, this.id);

            int affected = stmt.executeUpdate();

            if (affected > 0) {
                System.out.println("User removed from task.");
            } else {
                System.out.println("User was not linked to this task.");
            }

        } catch (SQLException e) {
            System.out.println("Error while removing user from task: " + e.getMessage());
        }
    }
}
