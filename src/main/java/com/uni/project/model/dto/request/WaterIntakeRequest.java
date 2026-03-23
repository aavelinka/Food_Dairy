package com.uni.project.model.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Request payload for water intake tracking")
public class WaterIntakeRequest {
    @NotNull
    @Positive
    @Schema(description = "User id", example = "1")
    private Integer userId;

    @NotNull
    @Schema(description = "Intake date", example = "2026-03-12")
    private LocalDate date;

    @NotNull
    @Positive
    @Schema(description = "Amount in milliliters", example = "250")
    private Integer amountMl;

    @Size(max = 30)
    @Schema(description = "Drink type", example = "Water")
    private String drinkType;

    @Size(max = 255)
    @Schema(description = "Optional comment", example = "After workout")
    private String comment;
}
