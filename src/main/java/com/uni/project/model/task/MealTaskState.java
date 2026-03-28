package com.uni.project.model.task;

import com.uni.project.model.dto.response.MealTaskStatusResponse;
import java.time.Instant;
import java.util.UUID;
import lombok.Getter;

@Getter
public class MealTaskState {
    private final UUID taskId;
    private final int totalItems;
    private final Instant createdAt;
    private volatile MealTaskStatus status;
    private volatile String errorMessage;
    private volatile Instant finishedAt;

    private MealTaskState(
            UUID taskId,
            int totalItems,
            Instant createdAt,
            MealTaskStatus status
    ) {
        this.taskId = taskId;
        this.totalItems = totalItems;
        this.createdAt = createdAt;
        this.status = status;
    }

    public static MealTaskState pending(UUID taskId, int totalItems) {
        return new MealTaskState(taskId, totalItems, Instant.now(), MealTaskStatus.PENDING);
    }

    public synchronized void markRunning() {
        status = MealTaskStatus.RUNNING;
        errorMessage = null;
        finishedAt = null;
    }

    public synchronized void markCompleted() {
        status = MealTaskStatus.COMPLETED;
        errorMessage = null;
        finishedAt = Instant.now();
    }

    public synchronized void markFailed(String errorMessage) {
        status = MealTaskStatus.FAILED;
        this.errorMessage = errorMessage;
        finishedAt = Instant.now();
    }

    public MealTaskStatusResponse toResponse() {
        return new MealTaskStatusResponse(taskId, status, totalItems, errorMessage, createdAt, finishedAt);
    }
}
