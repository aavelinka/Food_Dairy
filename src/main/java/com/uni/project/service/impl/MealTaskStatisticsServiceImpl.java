package com.uni.project.service.impl;

import com.uni.project.model.dto.response.MealTaskStatisticsResponse;
import com.uni.project.service.MealTaskStatisticsService;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import org.springframework.stereotype.Service;

@Service
public class MealTaskStatisticsServiceImpl implements MealTaskStatisticsService {
    private final AtomicInteger submittedTasks = new AtomicInteger();
    private final AtomicInteger runningTasks = new AtomicInteger();
    private final AtomicInteger completedTasks = new AtomicInteger();
    private final AtomicInteger failedTasks = new AtomicInteger();
    private final AtomicLong totalMealsSubmitted = new AtomicLong();

    @Override
    public void onTaskSubmitted(int mealCount) {
        submittedTasks.incrementAndGet();
        totalMealsSubmitted.addAndGet(mealCount);
    }

    @Override
    public void onTaskStarted() {
        runningTasks.incrementAndGet();
    }

    @Override
    public void onTaskCompleted() {
        runningTasks.decrementAndGet();
        completedTasks.incrementAndGet();
    }

    @Override
    public void onTaskFailed() {
        runningTasks.decrementAndGet();
        failedTasks.incrementAndGet();
    }

    @Override
    public MealTaskStatisticsResponse getStatistics() {
        return new MealTaskStatisticsResponse(
                submittedTasks.get(),
                runningTasks.get(),
                completedTasks.get(),
                failedTasks.get(),
                totalMealsSubmitted.get()
        );
    }
}
