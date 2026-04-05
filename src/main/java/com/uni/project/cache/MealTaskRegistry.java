package com.uni.project.cache;

import com.uni.project.model.task.MealTaskState;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.stereotype.Component;

@Component
public class MealTaskRegistry {
    private final Map<UUID, MealTaskState> tasks = new ConcurrentHashMap<>();

    public MealTaskState create(UUID taskId, int totalItems) {
        MealTaskState taskState = MealTaskState.pending(taskId, totalItems);
        tasks.put(taskId, taskState);
        return taskState;
    }

    public Optional<MealTaskState> findById(UUID taskId) {
        return Optional.ofNullable(tasks.get(taskId));
    }

    public void markRunning(UUID taskId) {
        getRequired(taskId).markRunning();
    }

    public void markCompleted(UUID taskId) {
        getRequired(taskId).markCompleted();
    }

    public void markFailed(UUID taskId, String errorMessage) {
        getRequired(taskId).markFailed(errorMessage);
    }

    private MealTaskState getRequired(UUID taskId) {
        return Optional.ofNullable(tasks.get(taskId))
                .orElseThrow(() -> new IllegalStateException("Task not found by Id: " + taskId));
    }
}
