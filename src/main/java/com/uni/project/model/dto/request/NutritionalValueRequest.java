package com.uni.project.model.dto.request;

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
public class NutritionalValueRequest {
    @NotNull
    @PositiveOrZero
    private Double calories;

    @NotNull
    @PositiveOrZero
    private Double proteins;

    @NotNull
    @PositiveOrZero
    private Double fats;

    @NotNull
    @PositiveOrZero
    private Double carbohydrates;

    private Integer ownerId;

    private Integer productId;

    private Integer mealId;
}
