package com.uni.project.model.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Schema(description = "Aggregated asynchronous meal task statistics")
public class MealTaskStatisticsResponse {
    @Schema(description = "Number of submitted asynchronous meal tasks", example = "3")
    private int submittedTasks;

    @Schema(description = "Number of tasks currently running", example = "1")
    private int runningTasks;

    @Schema(description = "Number of successfully completed tasks", example = "2")
    private int completedTasks;

    @Schema(description = "Number of failed tasks", example = "1")
    private int failedTasks;

    @Schema(description = "Total number of meals submitted to asynchronous tasks", example = "8")
    private long totalMealsSubmitted;
}
