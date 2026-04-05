package com.uni.project.model.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Schema(description = "Race condition demonstration result")
public class RaceConditionDemoResponse {
    @Schema(description = "Demo mode", example = "unsafe")
    private String mode;

    @Schema(description = "Number of worker threads", example = "50")
    private int threadCount;

    @Schema(description = "Number of increments performed by each thread", example = "1000")
    private int incrementsPerThread;

    @Schema(description = "Expected final counter value", example = "50000")
    private long expected;

    @Schema(description = "Actual final counter value", example = "47231")
    private long actual;

    @Schema(description = "How many updates were lost because of concurrent access", example = "2769")
    private long lostUpdates;
}
