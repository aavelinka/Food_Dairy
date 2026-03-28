package com.uni.project.model.dto.response;

import com.uni.project.model.task.MealTaskStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Schema(description = "Accepted asynchronous meal task")
public class MealTaskCreatedResponse {
    @Schema(description = "Created task id", example = "123e4567-e89b-12d3-a456-426614174000")
    private UUID taskId;

    @Schema(description = "Initial task status", example = "PENDING")
    private MealTaskStatus status;
}
