package com.uni.project.model.dto.response;

import com.uni.project.model.entity.BodyParameters;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserResponse {
    private Integer id;

    private String name;

    private String email;

    private BodyParameters measurements;

    private NutritionalValueResponse dailyGoal;

    private List<Integer> mealIds;
}
