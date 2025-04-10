package org.teamflow.models;

import org.teamflow.database.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;

public class Message {
    private int id;
    private int chatroomId;
    private int userId;
    private String content;
    private LocalDateTime timestamp;

    public Message() {}

    public Message(int id, int chatroomId, int userId, String content, LocalDateTime timestamp) {
        this.id = id;
        this.chatroomId = chatroomId;
        this.userId = userId;
        this.content = content;
        this.timestamp = timestamp;
    }

    public Message(int chatroomId, int userId, String content) {
        this.chatroomId = chatroomId;
        this.userId = userId;
        this.content = content;
        this.timestamp = LocalDateTime.now();
    }

    public static void insertMessage(Message message) {
        String insertSQL = "INSERT INTO message (chatroom_id, user_id, content, timestamp) VALUES (?, ?, ?, ?)";

        try (
                Connection conn = DatabaseConnection.getConnection();
                PreparedStatement insertStmt = conn.prepareStatement(insertSQL)
        ) {
            insertStmt.setInt(1, message.getChatroomId());
            insertStmt.setInt(2, message.getUserId());
            insertStmt.setString(3, message.getContent());
            insertStmt.setString(4, message.getTimestamp().toString());
            insertStmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Failed to save message: " + e.getMessage());
        }
    }

    public String getUserTitle() {
        String selectSQL = "SELECT username FROM user WHERE id = ?";

        try (
                Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(selectSQL)
        ) {
            stmt.setInt(1, getUserId());

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("username");
                }
            }

        } catch (SQLException e) {
            System.out.println("Failed to fetch username: " + e.getMessage());
        }

        return null;
    }


    public int getId() {
        return id;
    }

    public int getChatroomId() {
        return chatroomId;
    }

    public int getUserId() {
        return userId;
    }

    public String getContent() {
        return content;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setChatroomId(int chatroomId) {
        this.chatroomId = chatroomId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public String toString() {
        return "[" + timestamp + "] User " + userId + ": " + content;
    }
}
