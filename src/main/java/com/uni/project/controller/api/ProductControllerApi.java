package com.uni.project.controller.api;

import com.uni.project.model.dto.request.ProductRequest;
import com.uni.project.model.dto.response.ProductResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import java.util.List;
import org.springframework.http.ResponseEntity;

@Tag(name = "Products", description = "Operations with products")
public interface ProductControllerApi {
    @Operation(summary = "Get product by id")
    @ApiResponse(responseCode = "200", description = "Product found")
    @BadRequestApiResponse
    @NotFoundApiResponse
    @InternalServerErrorApiResponse
    ResponseEntity<ProductResponse> getProductById(@Parameter(description = "Product id") @Positive Integer id);

    @Operation(summary = "Get all products")
    @ApiResponse(responseCode = "200", description = "Products returned")
    @InternalServerErrorApiResponse
    ResponseEntity<List<ProductResponse>> getAllProducts();

    @Operation(summary = "Create product")
    @ApiResponse(responseCode = "201", description = "Product created")
    @BadRequestApiResponse
    @InternalServerErrorApiResponse
    ResponseEntity<ProductResponse> productCreate(@Valid ProductRequest productRequest);

    @Operation(summary = "Update product")
    @ApiResponse(responseCode = "200", description = "Product updated")
    @BadRequestApiResponse
    @NotFoundApiResponse
    @InternalServerErrorApiResponse
    ResponseEntity<ProductResponse> productUpdate(
            @Parameter(description = "Product id") @Positive Integer id,
            @Valid ProductRequest productRequest
    );

    @Operation(summary = "Delete product")
    @ApiResponse(responseCode = "204", description = "Product deleted")
    @BadRequestApiResponse
    @NotFoundApiResponse
    @InternalServerErrorApiResponse
    ResponseEntity<Void> productDelete(@Parameter(description = "Product id") @Positive Integer id);

    @Operation(summary = "Find products by exact name")
    @ApiResponse(responseCode = "200", description = "Products returned")
    @BadRequestApiResponse
    @InternalServerErrorApiResponse
    ResponseEntity<List<ProductResponse>> getAllProductsByName(
            @Parameter(description = "Exact product name") @NotBlank String nameSearch
    );

    @Operation(summary = "Find products by meal id")
    @ApiResponse(responseCode = "200", description = "Products returned")
    @BadRequestApiResponse
    @InternalServerErrorApiResponse
    ResponseEntity<List<ProductResponse>> getAllProductsByMeal(
            @Parameter(description = "Meal id") @Positive Integer mealId
    );
}
