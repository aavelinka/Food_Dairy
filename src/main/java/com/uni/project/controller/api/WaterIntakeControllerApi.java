package com.uni.project.controller.api;

import com.uni.project.model.dto.request.WaterIntakeRequest;
import com.uni.project.model.dto.response.WaterIntakeResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import java.time.LocalDate;
import java.util.List;
import org.springframework.http.ResponseEntity;

@Tag(name = "Water Intake", description = "Operations with daily water intake tracking")
public interface WaterIntakeControllerApi {
    @Operation(summary = "Create water intake entry")
    @ApiResponse(responseCode = "201", description = "Water intake created")
    @BadRequestApiResponse
    @InternalServerErrorApiResponse
    ResponseEntity<WaterIntakeResponse> waterIntakeCreate(@Valid WaterIntakeRequest waterIntakeRequest);

    @Operation(summary = "Get water intake by id")
    @ApiResponse(responseCode = "200", description = "Water intake found")
    @BadRequestApiResponse
    @NotFoundApiResponse
    @InternalServerErrorApiResponse
    ResponseEntity<WaterIntakeResponse> getWaterIntakeById(
            @Parameter(description = "Water intake id") @Positive Integer id
    );

    @Operation(summary = "Get all water intake entries")
    @ApiResponse(responseCode = "200", description = "Water intake entries returned")
    @InternalServerErrorApiResponse
    ResponseEntity<List<WaterIntakeResponse>> getAllWaterIntakes();

    @Operation(summary = "Update water intake entry")
    @ApiResponse(responseCode = "200", description = "Water intake updated")
    @BadRequestApiResponse
    @NotFoundApiResponse
    @InternalServerErrorApiResponse
    ResponseEntity<WaterIntakeResponse> waterIntakeUpdate(
            @Parameter(description = "Water intake id") @Positive Integer id,
            @Valid WaterIntakeRequest waterIntakeRequest
    );

    @Operation(summary = "Delete water intake entry")
    @ApiResponse(responseCode = "204", description = "Water intake deleted")
    @BadRequestApiResponse
    @NotFoundApiResponse
    @InternalServerErrorApiResponse
    ResponseEntity<Void> waterIntakeDelete(@Parameter(description = "Water intake id") @Positive Integer id);

    @Operation(summary = "Find water intake by user id")
    @ApiResponse(responseCode = "200", description = "Water intake entries returned")
    @BadRequestApiResponse
    @InternalServerErrorApiResponse
    ResponseEntity<List<WaterIntakeResponse>> getAllWaterIntakesByUserId(
            @Parameter(description = "User id") @Positive Integer userId
    );

    @Operation(summary = "Find water intake by user id and date")
    @ApiResponse(responseCode = "200", description = "Water intake entries returned")
    @BadRequestApiResponse
    @InternalServerErrorApiResponse
    ResponseEntity<List<WaterIntakeResponse>> getAllWaterIntakesByDate(
            @Parameter(description = "User id") @Positive Integer userId,
            @Parameter(description = "Intake date") LocalDate date
    );

    @Operation(summary = "Get daily total water intake")
    @ApiResponse(responseCode = "200", description = "Daily total returned")
    @BadRequestApiResponse
    @InternalServerErrorApiResponse
    ResponseEntity<Integer> getDailyTotalByUserIdAndDate(
            @Parameter(description = "User id") @Positive Integer userId,
            @Parameter(description = "Intake date") LocalDate date
    );
}
