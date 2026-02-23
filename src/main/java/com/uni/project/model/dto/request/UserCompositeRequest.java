package com.uni.project.model.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
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
public class UserCompositeRequest extends UserRequest {
    @NotNull
    @PositiveOrZero
    private Double dailyGoalCalories;

    @NotNull
    @PositiveOrZero
    private Double dailyGoalProteins;

    @NotNull
    @PositiveOrZero
    private Double dailyGoalFats;

    @NotNull
    @PositiveOrZero
    private Double dailyGoalCarbohydrates;

    @NotNull
    private LocalDate noteDate;

    @NotNull
    private List<String> noteTexts;

    private boolean failAfterUser;
}
