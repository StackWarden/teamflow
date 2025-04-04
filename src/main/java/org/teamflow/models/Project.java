package org.teamflow.models;

public class Project {
    private int id;
    private String name;
    private String description;

    public Project() {}

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

    @Override
    public String toString() {
        return "Project{id=" + id + ", name='" + name + "', description='" + description + "'}";
    }
}
