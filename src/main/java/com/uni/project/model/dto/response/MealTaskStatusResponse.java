package com.uni.project.model.dto.response;

import com.uni.project.model.task.MealTaskStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.Instant;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Schema(description = "Asynchronous meal task status")
public class MealTaskStatusResponse {
    @Schema(description = "Task id", example = "123e4567-e89b-12d3-a456-426614174000")
    private UUID taskId;

    @Schema(description = "Current task status", example = "RUNNING")
    private MealTaskStatus status;

    @Schema(description = "Number of meals in the submitted bulk request", example = "3")
    private int totalItems;

    @Schema(
            description = "Failure reason when task status is FAILED",
            example = "Forced error after saving 2 bulk meals"
    )
    private String errorMessage;

    @Schema(description = "Time when task was created", example = "2026-03-28T10:15:30Z")
    private Instant createdAt;

    @Schema(description = "Time when task finished", example = "2026-03-28T10:15:31Z")
    private Instant finishedAt;
}
