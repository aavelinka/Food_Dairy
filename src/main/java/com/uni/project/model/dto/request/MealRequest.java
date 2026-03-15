package com.uni.project.model.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Request payload for creating or updating a meal")
public class MealRequest {
    @NotBlank
    @Size(max = 50)
    @Schema(description = "Meal name", example = "Lunch")
    private String name;

    @NotNull
    @Schema(description = "Meal date", example = "2026-03-12")
    private LocalDate date;

    @Valid
    @Schema(description = "Total nutritional value for the meal")
    private NutritionalValueRequest totalNutritional;

    @Positive
    @Schema(description = "Author user id", example = "1")
    private Integer authorId;

    @Schema(description = "Product ids included in the meal")
    private List<@Positive Integer> productIds;
}
