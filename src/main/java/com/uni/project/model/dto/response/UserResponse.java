package com.uni.project.model.dto.response;

import com.uni.project.model.entity.GoalType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "User response")
public class UserResponse {
    @Schema(description = "User id", example = "1")
    private Integer id;

    @Schema(description = "User name", example = "Anton")
    private String name;

    @Schema(description = "User email", example = "anton@example.com")
    private String email;

    @Schema(description = "Nutrition goal", example = "MAINTENANCE")
    private GoalType goalType;

    @Schema(description = "Related meal ids")
    private List<Integer> mealIds;

    @Schema(description = "Related body parameters ids")
    private List<Integer> bodyParametersIds;
}
