package com.uni.project.model.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Request payload for creating or updating a product")
public class ProductRequest {
    @NotBlank
    @Size(max = 100)
    @Schema(description = "Product name", example = "Chicken breast")
    private String name;

    @NotNull
    @Valid
    @Schema(description = "Nutrition values per 100 grams")
    private NutritionalValueRequest nutritionalValue100g;
}
