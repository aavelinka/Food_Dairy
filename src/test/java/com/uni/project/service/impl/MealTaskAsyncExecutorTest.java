package com.uni.project.service.impl;

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
import com.uni.project.service.MealService;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
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

    @InjectMocks
    private MealTaskAsyncExecutor mealTaskAsyncExecutor;

    @Test
    void createBulkTxShouldMarkTaskCompletedWhenMealCreationSucceeds() {
        UUID taskId = UUID.randomUUID();
        List<MealRequest> requests = List.of(buildMealRequest());

        CompletableFuture<Void> result = mealTaskAsyncExecutor.createBulkTx(taskId, requests, null);

        result.join();

        verify(mealTaskRegistry).markRunning(taskId);
        verify(mealService).createBulkTx(requests, null);
        verify(mealTaskRegistry).markCompleted(taskId);
        verify(mealTaskRegistry, never()).markFailed(any(), any());
        assertTrue(result.isDone());
    }

    @Test
    void createBulkTxShouldMarkTaskFailedWhenMealCreationThrows() {
        UUID taskId = UUID.randomUUID();
        List<MealRequest> requests = List.of(buildMealRequest());
        when(mealService.createBulkTx(requests, 1))
                .thenThrow(new BulkMealCreationException("Forced error after saving 1 bulk meals"));

        CompletableFuture<Void> result = mealTaskAsyncExecutor.createBulkTx(taskId, requests, 1);

        result.join();

        verify(mealTaskRegistry).markRunning(taskId);
        verify(mealService).createBulkTx(requests, 1);
        verify(mealTaskRegistry, never()).markCompleted(taskId);
        verify(mealTaskRegistry).markFailed(eq(taskId), eq("Forced error after saving 1 bulk meals"));
        assertTrue(result.isDone());
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
