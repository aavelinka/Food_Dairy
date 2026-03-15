package com.uni.project.model.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Nutritional values response")
public class NutritionalValueResponse {
    @Schema(description = "Calories", example = "450.0")
    private Double calories;

    @Schema(description = "Proteins in grams", example = "30.0")
    private Double proteins;

    @Schema(description = "Fats in grams", example = "12.0")
    private Double fats;

    @Schema(description = "Carbohydrates in grams", example = "48.0")
    private Double carbohydrates;
}
