package com.uni.project.model.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class NutritionalValueResponse {
    private Integer id;

    private Double calories;

    private Double proteins;

    private Double fats;

    private Double carbohydrates;

    private Integer ownerId;

    private Integer productId;

    private Integer mealId;
}
