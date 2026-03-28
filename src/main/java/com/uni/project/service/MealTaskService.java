package com.uni.project.service;

import com.uni.project.model.dto.request.MealRequest;
import com.uni.project.model.dto.response.MealTaskCreatedResponse;
import com.uni.project.model.dto.response.MealTaskStatusResponse;
import java.util.List;
import java.util.UUID;

public interface MealTaskService {
    MealTaskCreatedResponse startBulkTxTask(List<MealRequest> mealRequests, Integer failAfterIndex);

    MealTaskStatusResponse getTaskStatus(UUID taskId);
}
