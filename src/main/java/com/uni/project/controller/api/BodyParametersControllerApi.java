package com.uni.project.controller.api;

import com.uni.project.model.dto.request.BodyParametersRequest;
import com.uni.project.model.dto.request.NutritionalValueRequest;
import com.uni.project.model.dto.response.BodyParametersResponse;
import com.uni.project.model.dto.response.NutritionalValueResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import java.time.LocalDate;
import java.util.List;
import org.springframework.http.ResponseEntity;

@Tag(name = "Body Parameters", description = "Operations with body measurements and nutritional goals")
public interface BodyParametersControllerApi {
    @Operation(summary = "Create body parameters")
    @ApiResponse(responseCode = "201", description = "Body parameters created")
    @BadRequestApiResponse
    @NotFoundApiResponse
    @InternalServerErrorApiResponse
    ResponseEntity<BodyParametersResponse> bodyParametersCreate(@Valid BodyParametersRequest bodyParametersRequest);

    @Operation(summary = "Get body parameters by id")
    @ApiResponse(responseCode = "200", description = "Body parameters found")
    @BadRequestApiResponse
    @NotFoundApiResponse
    @InternalServerErrorApiResponse
    ResponseEntity<BodyParametersResponse> getBodyParametersById(
            @Parameter(description = "Body parameters id") @Positive Integer id
    );

    @Operation(summary = "Get all body parameters")
    @ApiResponse(responseCode = "200", description = "Body parameters returned")
    @InternalServerErrorApiResponse
    ResponseEntity<List<BodyParametersResponse>> getAllBodyParameters();

    @Operation(summary = "Update body parameters")
    @ApiResponse(responseCode = "200", description = "Body parameters updated")
    @BadRequestApiResponse
    @NotFoundApiResponse
    @InternalServerErrorApiResponse
    ResponseEntity<BodyParametersResponse> bodyParametersUpdate(
            @Parameter(description = "Body parameters id") @Positive Integer id,
            @Valid BodyParametersRequest bodyParametersRequest
    );

    @Operation(summary = "Delete body parameters")
    @ApiResponse(responseCode = "204", description = "Body parameters deleted")
    @BadRequestApiResponse
    @NotFoundApiResponse
    @InternalServerErrorApiResponse
    ResponseEntity<Void> bodyParametersDelete(
            @Parameter(description = "Body parameters id") @Positive Integer id
    );

    @Operation(summary = "Find body parameters by user id")
    @ApiResponse(responseCode = "200", description = "Body parameters returned")
    @BadRequestApiResponse
    @InternalServerErrorApiResponse
    ResponseEntity<List<BodyParametersResponse>> getAllBodyParametersByUserId(
            @Parameter(description = "User id") @Positive Integer userId
    );

    @Operation(summary = "Find body parameters by user id and date")
    @ApiResponse(responseCode = "200", description = "Body parameters returned")
    @BadRequestApiResponse
    @InternalServerErrorApiResponse
    ResponseEntity<List<BodyParametersResponse>> getAllBodyParametersByUserIdAndDate(
            @Parameter(description = "User id") @Positive Integer userId,
            @Parameter(description = "Measurement date") LocalDate date
    );

    @Operation(summary = "Auto-calculate nutritional goal for user")
    @ApiResponse(responseCode = "200", description = "Nutritional goal calculated")
    @BadRequestApiResponse
    @NotFoundApiResponse
    @InternalServerErrorApiResponse
    ResponseEntity<NutritionalValueResponse> calculateNutritionalValueForUser(
            @Parameter(description = "User id") @Positive Integer id
    );

    @Operation(summary = "Set manual nutritional goal")
    @ApiResponse(responseCode = "200", description = "Nutritional goal updated")
    @BadRequestApiResponse
    @NotFoundApiResponse
    @InternalServerErrorApiResponse
    ResponseEntity<NutritionalValueResponse> setManualNutritionalValue(
            @Parameter(description = "Body parameters id") @Positive Integer id,
            @Valid NutritionalValueRequest request
    );
}
