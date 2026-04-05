package com.uni.project.controller.api;

import com.uni.project.model.dto.response.RaceConditionDemoResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Positive;
import org.springframework.http.ResponseEntity;

@Tag(name = "Demo", description = "Concurrency and race condition demonstrations")
public interface DemoControllerApi {
    @Operation(summary = "Run unsafe race condition demo with a shared non-thread-safe counter")
    @ApiResponse(responseCode = "200", description = "Unsafe demo finished")
    @BadRequestApiResponse
    @InternalServerErrorApiResponse
    ResponseEntity<RaceConditionDemoResponse> runUnsafeRaceConditionDemo(
            @Parameter(description = "Number of concurrent worker threads, minimum 50") @Min(50) Integer threadCount,
            @Parameter(description = "Number of increments per worker") @Positive Integer incrementsPerThread
    );

    @Operation(summary = "Run atomic race condition demo with a thread-safe counter")
    @ApiResponse(responseCode = "200", description = "Atomic demo finished")
    @BadRequestApiResponse
    @InternalServerErrorApiResponse
    ResponseEntity<RaceConditionDemoResponse> runAtomicRaceConditionDemo(
            @Parameter(description = "Number of concurrent worker threads, minimum 50") @Min(50) Integer threadCount,
            @Parameter(description = "Number of increments per worker") @Positive Integer incrementsPerThread
    );
}
