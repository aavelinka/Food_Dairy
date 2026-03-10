package com.uni.project.model.dto.request;

import com.uni.project.model.entity.Sex;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserBodyParametersRequest {
    @NotNull
    private LocalDate recordDate;

    @NotNull
    private Sex sex;

    @NotNull
    @Positive
    private Double weight;

    @NotNull
    @Positive
    private Double height;

    @NotNull
    @Positive
    private Integer age;

    @PositiveOrZero
    private Double chest;

    @PositiveOrZero
    private Double waist;

    @PositiveOrZero
    private Double hips;
}
