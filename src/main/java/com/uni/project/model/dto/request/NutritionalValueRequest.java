package com.uni.project.model.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Nutritional values")
public class NutritionalValueRequest {
    @NotNull
    @PositiveOrZero
    @Schema(description = "Calories", example = "450.0")
    private Double calories;

    @NotNull
    @PositiveOrZero
    @Schema(description = "Proteins in grams", example = "30.0")
    private Double proteins;

    @NotNull
    @PositiveOrZero
    @Schema(description = "Fats in grams", example = "12.0")
    private Double fats;

    @NotNull
    @PositiveOrZero
    @Schema(description = "Carbohydrates in grams", example = "48.0")
    private Double carbohydrates;
}
