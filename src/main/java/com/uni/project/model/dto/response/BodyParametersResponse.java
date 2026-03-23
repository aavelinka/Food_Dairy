package com.uni.project.model.dto.response;

import com.uni.project.model.entity.Sex;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Body parameters response")
public class BodyParametersResponse {
    @Schema(description = "Body parameters id", example = "1")
    private Integer id;

    @Schema(description = "Owner user id", example = "1")
    private Integer userId;

    @Schema(description = "Measurement date", example = "2026-03-12")
    private LocalDate recordDate;

    @Schema(description = "Biological sex", example = "MALE")
    private Sex sex;

    @Schema(description = "Weight in kilograms", example = "72.5")
    private Double weight;

    @Schema(description = "Height in centimeters", example = "178.0")
    private Double height;

    @Schema(description = "Age in years", example = "23")
    private Integer age;

    @Schema(description = "Chest circumference", example = "96.0")
    private Double chest;

    @Schema(description = "Waist circumference", example = "78.0")
    private Double waist;

    @Schema(description = "Hips circumference", example = "94.0")
    private Double hips;

    @Schema(description = "Daily nutritional goal")
    private NutritionalValueResponse dailyGoal;

    @Schema(description = "Was nutritional goal auto-calculated", example = "true")
    private Boolean autoCalculated;
}
