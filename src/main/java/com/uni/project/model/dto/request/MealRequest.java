package com.uni.project.model.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MealRequest {
    @NotBlank
    private String name;

    @NotNull
    private LocalDate date;

    @Valid
    private NutritionalValueRequest totalNutritional;

    private Integer authorId;
}
