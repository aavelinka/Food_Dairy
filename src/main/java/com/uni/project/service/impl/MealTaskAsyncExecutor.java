package com.uni.project.service.impl;

import com.uni.project.cache.MealTaskRegistry;
import com.uni.project.model.dto.request.MealRequest;
import com.uni.project.service.MealService;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@AllArgsConstructor
public class MealTaskAsyncExecutor {
    private final MealService mealService;
    private final MealTaskRegistry mealTaskRegistry;

    @Async("mealTaskExecutor")
    public CompletableFuture<Void> createBulkTx(
            UUID taskId,
            List<MealRequest> mealRequests,
            Integer failAfterIndex
    ) {
        mealTaskRegistry.markRunning(taskId);
        try {
            mealService.createBulkTx(mealRequests, failAfterIndex);
            mealTaskRegistry.markCompleted(taskId);
        } catch (Exception ex) {
            log.error("Async meal task {} failed", taskId, ex);
            mealTaskRegistry.markFailed(taskId, ex.getMessage());
        }
        return CompletableFuture.completedFuture(null);
    }
}
