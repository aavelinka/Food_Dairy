package com.uni.project.model.dto.response;

import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class WaterIntakeResponse {
    private Integer id;

    private Integer userId;

    private LocalDate date;

    private Integer amountMl;

    private String drinkType;

    private String comment;
}
