package org.teamflow.models;

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
        this.timestamp = LocalDateTime.now(); // automatisch nu
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
