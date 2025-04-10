package org.teamflow.models;

public class UserStory {
    private int id;
    private int epicId;
    private String description;

    public UserStory() {}

    public UserStory(int id, int epicId, String description) {
        this.id = id;
        this.epicId = epicId;
        this.description = description;
    }

    public UserStory(int epicId, String description) {
        this.epicId = epicId;
        this.description = description;
    }

    public int getId() {
        return id;
    }

    public int getEpicId() {
        return epicId;
    }

    public String getDescription() {
        return description;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setEpicId(int epicId) {
        this.epicId = epicId;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return "UserStory{" +
                "id=" + id +
                ", epicId=" + epicId +
                ", description='" + description + '\'' +
                '}';
    }
}
