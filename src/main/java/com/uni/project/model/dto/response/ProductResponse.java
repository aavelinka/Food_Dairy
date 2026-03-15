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
@Schema(description = "Product response")
public class ProductResponse {
    @Schema(description = "Product id", example = "1")
    private Integer id;

    @Schema(description = "Product name", example = "Chicken breast")
    private String name;

    @Schema(description = "Nutrition values per 100 grams")
    private NutritionalValueResponse nutritionalValue100g;
}
