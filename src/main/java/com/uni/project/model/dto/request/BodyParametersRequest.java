package com.uni.project.model.dto.request;

import com.uni.project.model.entity.Sex;
import io.swagger.v3.oas.annotations.media.Schema;
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
@Schema(description = "Request payload for body measurements")
public class BodyParametersRequest {
    @NotNull
    @Schema(description = "Measurement date", example = "2026-03-12")
    private LocalDate recordDate;

    @NotNull
    @Schema(description = "Biological sex", example = "MALE")
    private Sex sex;

    @NotNull
    @Positive
    @Schema(description = "Weight in kilograms", example = "72.5")
    private Double weight;

    @NotNull
    @Positive
    @Schema(description = "Height in centimeters", example = "178.0")
    private Double height;

    @NotNull
    @Positive
    @Schema(description = "Age in years", example = "23")
    private Integer age;

    @PositiveOrZero
    @Schema(description = "Chest circumference in centimeters", example = "96.0")
    private Double chest;

    @PositiveOrZero
    @Schema(description = "Waist circumference in centimeters", example = "78.0")
    private Double waist;

    @PositiveOrZero
    @Schema(description = "Hips circumference in centimeters", example = "94.0")
    private Double hips;

    @Positive
    @Schema(description = "Owner user id", example = "1")
    private Integer userId;
}
