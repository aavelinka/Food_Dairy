package com.uni.project.model.dto.request;

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
public class WaterIntakeRequest {
    @NotNull
    @Positive
    private Integer userId;

    @NotNull
    private LocalDate date;

    @NotNull
    @Positive
    private Integer amountMl;

    @Size(max = 30)
    private String drinkType;

    @Size(max = 255)
    private String comment;
}
