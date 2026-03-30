package com.uni.project.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.uni.project.cache.MealTaskRegistry;
import com.uni.project.exception.BulkMealCreationException;
import com.uni.project.model.dto.request.MealRequest;
import com.uni.project.model.dto.request.NutritionalValueRequest;
import com.uni.project.model.task.MealTaskStatus;
import com.uni.project.service.MealService;
import com.uni.project.service.MealTaskStatisticsService;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class MealTaskAsyncExecutorTest {
    @Mock
    private MealService mealService;

    @Mock
    private MealTaskRegistry mealTaskRegistry;

    @Mock
    private MealTaskStatisticsService mealTaskStatisticsService;

    @InjectMocks
    private MealTaskAsyncExecutor mealTaskAsyncExecutor;

    @Test
    void createBulkTxShouldMarkTaskCompletedWhenMealCreationSucceeds() {
        UUID taskId = UUID.randomUUID();
        List<MealRequest> requests = List.of(buildMealRequest());

        CompletableFuture<Void> result = mealTaskAsyncExecutor.createBulkTx(taskId, requests, null, null);

        result.join();

        verify(mealTaskRegistry).markRunning(taskId);
        verify(mealTaskStatisticsService).onTaskStarted();
        verify(mealService).createBulkTx(requests, null);
        verify(mealTaskRegistry).markCompleted(taskId);
        verify(mealTaskStatisticsService).onTaskCompleted();
        verify(mealTaskRegistry, never()).markFailed(any(), any());
        verify(mealTaskStatisticsService, never()).onTaskFailed();
        assertTrue(result.isDone());
    }

    @Test
    void createBulkTxShouldMarkTaskFailedWhenMealCreationThrows() {
        UUID taskId = UUID.randomUUID();
        List<MealRequest> requests = List.of(buildMealRequest());
        when(mealService.createBulkTx(requests, 1))
                .thenThrow(new BulkMealCreationException("Forced error after saving 1 bulk meals"));

        CompletableFuture<Void> result = mealTaskAsyncExecutor.createBulkTx(taskId, requests, 1, null);

        result.join();

        verify(mealTaskRegistry).markRunning(taskId);
        verify(mealTaskStatisticsService).onTaskStarted();
        verify(mealService).createBulkTx(requests, 1);
        verify(mealTaskRegistry, never()).markCompleted(taskId);
        verify(mealTaskStatisticsService, never()).onTaskCompleted();
        verify(mealTaskRegistry).markFailed(eq(taskId), eq("Forced error after saving 1 bulk meals"));
        verify(mealTaskStatisticsService).onTaskFailed();
        assertTrue(result.isDone());
    }

    @Test
    void createBulkTxShouldKeepTaskRunningDuringArtificialDelay() throws InterruptedException {
        MealTaskRegistry registry = new MealTaskRegistry();
        MealTaskAsyncExecutor executor = new MealTaskAsyncExecutor(mealService, registry, mealTaskStatisticsService);
        UUID taskId = UUID.randomUUID();
        List<MealRequest> requests = List.of(buildMealRequest());
        registry.create(taskId, requests.size());

        CompletableFuture<Void> result = CompletableFuture.runAsync(
                () -> executor.createBulkTx(taskId, requests, null, 200L).join()
        );

        MealTaskStatus currentStatus = registry.findById(taskId).orElseThrow().getStatus();
        long deadlineNanos = System.nanoTime() + TimeUnit.SECONDS.toNanos(1);
        while (currentStatus == MealTaskStatus.PENDING && System.nanoTime() < deadlineNanos) {
            Thread.sleep(10);
            currentStatus = registry.findById(taskId).orElseThrow().getStatus();
        }

        assertEquals(MealTaskStatus.RUNNING, currentStatus);

        result.join();

        assertEquals(MealTaskStatus.COMPLETED, registry.findById(taskId).orElseThrow().getStatus());
        verify(mealService).createBulkTx(requests, null);
    }

    private MealRequest buildMealRequest() {
        return new MealRequest(
                "Lunch",
                LocalDate.of(2026, 3, 18),
                new NutritionalValueRequest(500.0, 30.0, 15.0, 55.0),
                null,
                List.of()
        );
    }
}
