package org.teamflow.models;

public class ProjectCreationResult {
    private final int status; // 1 = success, 2 = already exists, 0 = error
    private final Project project;

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

    public boolean isSuccess() {
        return status == 1;
    }
}
