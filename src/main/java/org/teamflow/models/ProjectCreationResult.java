package org.teamflow.models;

public class ProjectCreationResult {
    private int status;
    private Project project;

    public ProjectCreationResult(int status, Project project) {
        this.status = status;
        this.project = project;
    }

    public int getStatus() {
        return status;
    }

    public Project getProject() {
        return project;
    }
}
