package com.uni.project.model.dto.response;

import com.uni.project.model.entity.Sex;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BodyParametersResponse {
    private Integer id;

    private Integer userId;

    private LocalDate recordDate;

    private Sex sex;

    private Double weight;

    private Double height;

    private Integer age;

    private Double chest;

    private Double waist;

    private Double hips;

    private NutritionalValueResponse dailyGoal;
}
