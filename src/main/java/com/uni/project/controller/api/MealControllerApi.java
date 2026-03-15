package com.uni.project.controller.api;

import com.uni.project.model.dto.request.MealRequest;
import com.uni.project.model.dto.response.MealResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import java.util.List;
import org.springframework.http.ResponseEntity;

@Tag(name = "Meals", description = "Operations with meals")
public interface MealControllerApi {
    @Operation(summary = "Get meal by id")
    @ApiResponse(responseCode = "200", description = "Meal found")
    @BadRequestApiResponse
    @NotFoundApiResponse
    @InternalServerErrorApiResponse
    ResponseEntity<MealResponse> getMealById(@Parameter(description = "Meal id") @Positive Integer id);

    @Operation(summary = "Get all meals")
    @ApiResponse(responseCode = "200", description = "Meals returned")
    @InternalServerErrorApiResponse
    ResponseEntity<List<MealResponse>> getAllMeals();

    @Operation(summary = "Create meal")
    @ApiResponse(responseCode = "201", description = "Meal created")
    @BadRequestApiResponse
    @NotFoundApiResponse
    @InternalServerErrorApiResponse
    ResponseEntity<MealResponse> mealCreate(@Valid MealRequest mealRequest);

    @Operation(summary = "Update meal")
    @ApiResponse(responseCode = "200", description = "Meal updated")
    @BadRequestApiResponse
    @NotFoundApiResponse
    @InternalServerErrorApiResponse
    ResponseEntity<MealResponse> mealUpdate(
            @Parameter(description = "Meal id") @Positive Integer id,
            @Valid MealRequest mealRequest
    );

    @Operation(summary = "Delete meal")
    @ApiResponse(responseCode = "204", description = "Meal deleted")
    @BadRequestApiResponse
    @NotFoundApiResponse
    @InternalServerErrorApiResponse
    ResponseEntity<Void> mealDelete(@Parameter(description = "Meal id") @Positive Integer id);

    @Operation(summary = "Find meals by exact name")
    @ApiResponse(responseCode = "200", description = "Meals returned")
    @BadRequestApiResponse
    @InternalServerErrorApiResponse
    ResponseEntity<List<MealResponse>> getAllMealsByName(
            @Parameter(description = "Exact meal name") @NotBlank String nameSearch
    );

    @Operation(summary = "Find meals by author id")
    @ApiResponse(responseCode = "200", description = "Meals returned")
    @BadRequestApiResponse
    @InternalServerErrorApiResponse
    ResponseEntity<List<MealResponse>> getAllMealsByAuthor(
            @Parameter(description = "Author id") @Positive Integer authorId
    );

    @Operation(summary = "Find meals containing all requested products")
    @ApiResponse(responseCode = "200", description = "Meals returned")
    @BadRequestApiResponse
    @InternalServerErrorApiResponse
    ResponseEntity<List<MealResponse>> getAllMealsByProductList(
            @Parameter(description = "Product ids") List<@Positive Integer> productIds
    );
}
