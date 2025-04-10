package org.teamflow.models;

public class EpicCreationResult {
    private final int status; // 1 = success, 2 = already exists, 0 = error
    private final Epic epic;

    public EpicCreationResult(int status, Epic epic) {
        this.status = status;
        this.epic = epic;
    }

    public int getStatus() {
        return status;
    }

    public Epic getEpic() {
        return epic;
    }

    public boolean isSuccess() {
        return status == 1;
    }
}
