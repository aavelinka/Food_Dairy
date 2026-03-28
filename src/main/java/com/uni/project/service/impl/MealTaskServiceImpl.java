package com.uni.project.service.impl;

import com.uni.project.cache.MealTaskRegistry;
import com.uni.project.exception.TaskNotFoundException;
import com.uni.project.model.dto.request.MealRequest;
import com.uni.project.model.dto.response.MealTaskCreatedResponse;
import com.uni.project.model.dto.response.MealTaskStatusResponse;
import com.uni.project.model.task.MealTaskState;
import com.uni.project.service.MealTaskService;
import java.util.List;
import java.util.UUID;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class MealTaskServiceImpl implements MealTaskService {
    private static final String TASK_NOT_FOUND_MESSAGE = "Task not found by Id";

    private final MealTaskRegistry mealTaskRegistry;
    private final MealTaskAsyncExecutor mealTaskAsyncExecutor;

    @Override
    public MealTaskCreatedResponse startBulkTxTask(List<MealRequest> mealRequests, Integer failAfterIndex) {
        UUID taskId = UUID.randomUUID();
        List<MealRequest> requestSnapshot = List.copyOf(mealRequests);
        MealTaskState taskState = mealTaskRegistry.create(taskId, requestSnapshot.size());
        mealTaskAsyncExecutor.createBulkTx(taskId, requestSnapshot, failAfterIndex);
        return new MealTaskCreatedResponse(taskState.getTaskId(), taskState.getStatus());
    }

    @Override
    public MealTaskStatusResponse getTaskStatus(UUID taskId) {
        return mealTaskRegistry.findById(taskId)
                .map(MealTaskState::toResponse)
                .orElseThrow(() -> new TaskNotFoundException(TASK_NOT_FOUND_MESSAGE));
    }
}
