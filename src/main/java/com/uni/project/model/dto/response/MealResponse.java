package com.uni.project.model.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Meal response")
public class MealResponse {
    @Schema(description = "Meal id", example = "1")
    private Integer id;

    @Schema(description = "Meal name", example = "Lunch")
    private String name;

    @Schema(description = "Meal date", example = "2026-03-12")
    private LocalDate date;

    @Schema(description = "Total nutritional values")
    private NutritionalValueResponse totalNutritional;

    @Schema(description = "Author user id", example = "1")
    private Integer authorId;

    @Schema(description = "Included product ids")
    private List<Integer> productIds;

    @Schema(description = "Attached recipe note id", example = "5")
    private Integer recipeId;
}
