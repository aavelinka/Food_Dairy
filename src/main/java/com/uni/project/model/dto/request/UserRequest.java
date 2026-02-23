package com.uni.project.model.dto.request;

import com.uni.project.model.entity.BodyParameters;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserRequest {
    @NotBlank
    private String name;

    @NotBlank
    private String password;

    @NotBlank
    @Email
    private String email;

    @NotNull
    @Valid
    private BodyParameters measurements;

    private Integer dailyGoalId;

    private List<Integer> mealIds;

    private List<Integer> postIds;
}
