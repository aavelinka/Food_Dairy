package com.uni.project.cache;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.uni.project.model.task.MealTaskStatus;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class MealTaskRegistryTest {
    private final MealTaskRegistry mealTaskRegistry = new MealTaskRegistry();

    @Test
    void createShouldStorePendingTask() {
        UUID taskId = UUID.randomUUID();

        mealTaskRegistry.create(taskId, 3);

        assertTrue(mealTaskRegistry.findById(taskId).isPresent());
        assertEquals(MealTaskStatus.PENDING, mealTaskRegistry.findById(taskId).orElseThrow().getStatus());
        assertEquals(3, mealTaskRegistry.findById(taskId).orElseThrow().getTotalItems());
        assertNull(mealTaskRegistry.findById(taskId).orElseThrow().getFinishedAt());
    }

    @Test
    void markCompletedShouldUpdateTaskState() {
        UUID taskId = UUID.randomUUID();
        mealTaskRegistry.create(taskId, 2);

        mealTaskRegistry.markRunning(taskId);
        mealTaskRegistry.markCompleted(taskId);

        assertEquals(MealTaskStatus.COMPLETED, mealTaskRegistry.findById(taskId).orElseThrow().getStatus());
        assertNull(mealTaskRegistry.findById(taskId).orElseThrow().getErrorMessage());
        assertNotNull(mealTaskRegistry.findById(taskId).orElseThrow().getFinishedAt());
    }

    @Test
    void markFailedShouldStoreErrorMessage() {
        UUID taskId = UUID.randomUUID();
        mealTaskRegistry.create(taskId, 1);

        mealTaskRegistry.markFailed(taskId, "bulk failed");

        assertEquals(MealTaskStatus.FAILED, mealTaskRegistry.findById(taskId).orElseThrow().getStatus());
        assertEquals("bulk failed", mealTaskRegistry.findById(taskId).orElseThrow().getErrorMessage());
    }

    @Test
    void markRunningShouldThrowWhenTaskDoesNotExist() {
        UUID taskId = UUID.randomUUID();

        IllegalStateException exception = assertThrows(
                IllegalStateException.class,
                () -> mealTaskRegistry.markRunning(taskId)
        );

        assertEquals("Task not found by Id: " + taskId, exception.getMessage());
    }
}
