package com.uni.project.service;

import com.uni.project.model.dto.response.MealTaskStatisticsResponse;

public interface MealTaskStatisticsService {
    void onTaskSubmitted(int mealCount);

    void onTaskStarted();

    void onTaskCompleted();

    void onTaskFailed();

    MealTaskStatisticsResponse getStatistics();
}
