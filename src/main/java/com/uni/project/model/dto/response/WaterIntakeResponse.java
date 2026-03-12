package com.uni.project.model.dto.response;

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
@Schema(description = "Water intake response")
public class WaterIntakeResponse {
    @Schema(description = "Water intake id", example = "1")
    private Integer id;

    @Schema(description = "User id", example = "1")
    private Integer userId;

    @Schema(description = "Intake date", example = "2026-03-12")
    private LocalDate date;

    @Schema(description = "Amount in milliliters", example = "250")
    private Integer amountMl;

    @Schema(description = "Drink type", example = "Water")
    private String drinkType;

    @Schema(description = "Optional comment", example = "After workout")
    private String comment;
}
