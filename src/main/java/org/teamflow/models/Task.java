package org.teamflow.models;

public class Task {
    private int id;
    private String title;
    private String status;
    private int storyId;

    public Task() {}

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
}
