package com.uni.project.model.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
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
public class MealRequest {
    @NotBlank
    private String name;

    @NotNull
    private LocalDate date;

    private Integer totalNutritionalId;

    private Integer authorId;

    private List<Integer> productIds;

    private Integer recipeId;
}
