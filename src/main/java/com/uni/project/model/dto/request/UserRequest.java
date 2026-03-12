package com.uni.project.model.dto.request;

import com.uni.project.model.entity.GoalType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Request payload for creating or updating a user")
public class UserRequest {
    @NotBlank
    @Size(max = 50)
    @Schema(description = "User name", example = "Anton")
    private String name;

    @NotBlank
    @Size(min = 6, max = 255)
    @Schema(description = "User password", example = "secret123")
    private String password;

    @NotBlank
    @Email
    @Size(max = 50)
    @Schema(description = "User email", example = "anton@example.com")
    private String email;

    @NotNull
    @Valid
    @Schema(description = "Initial body parameters")
    private BodyParametersRequest measurements;

    @NotNull
    @Schema(description = "Nutrition goal", example = "MAINTENANCE")
    private GoalType goalType;
}
