package com.uni.project.model.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MealResponse {
    private Integer id;

    private String name;

    private Integer totalNutritionalId;

    private Integer authorId;

    private List<Integer> productIds;

    private Integer recipeId;
}
