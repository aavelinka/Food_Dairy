package com.uni.project.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.uni.project.model.dto.response.MealTaskStatisticsResponse;
import org.junit.jupiter.api.Test;

class MealTaskStatisticsServiceImplTest {
    private final MealTaskStatisticsServiceImpl mealTaskStatisticsService = new MealTaskStatisticsServiceImpl();

    @Test
    void onTaskSubmittedShouldIncreaseSubmittedCounters() {
        mealTaskStatisticsService.onTaskSubmitted(3);

        MealTaskStatisticsResponse response = mealTaskStatisticsService.getStatistics();

        assertEquals(1, response.getSubmittedTasks());
        assertEquals(0, response.getRunningTasks());
        assertEquals(0, response.getCompletedTasks());
        assertEquals(0, response.getFailedTasks());
        assertEquals(3L, response.getTotalMealsSubmitted());
    }

    @Test
    void onTaskCompletedShouldMoveCountersFromRunningToCompleted() {
        mealTaskStatisticsService.onTaskStarted();
        mealTaskStatisticsService.onTaskCompleted();

        MealTaskStatisticsResponse response = mealTaskStatisticsService.getStatistics();

        assertEquals(0, response.getRunningTasks());
        assertEquals(1, response.getCompletedTasks());
    }

    @Test
    void onTaskFailedShouldMoveCountersFromRunningToFailed() {
        mealTaskStatisticsService.onTaskStarted();
        mealTaskStatisticsService.onTaskFailed();

        MealTaskStatisticsResponse response = mealTaskStatisticsService.getStatistics();

        assertEquals(0, response.getRunningTasks());
        assertEquals(1, response.getFailedTasks());
    }
}
