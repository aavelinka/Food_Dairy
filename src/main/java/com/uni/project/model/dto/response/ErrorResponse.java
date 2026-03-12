package com.uni.project.model.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@Schema(description = "Unified API error response")
public class ErrorResponse {
    @Schema(description = "Time when the error occurred", example = "2026-03-12T14:30:00Z")
    private final Instant timestamp;

    @Schema(description = "HTTP status code", example = "400")
    private final int status;

    @Schema(description = "HTTP status reason phrase", example = "Bad Request")
    private final String error;

    @Schema(description = "Human-readable error message", example = "Validation failed")
    private final String message;

    @Schema(description = "Request path that caused the error", example = "/api/users/1")
    private final String path;

    @Builder.Default
    @Schema(description = "Field-level validation or business errors")
    private final Map<String, String> fieldErrors = new LinkedHashMap<>();
}
