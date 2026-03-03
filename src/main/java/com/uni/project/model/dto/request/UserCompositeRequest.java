package com.uni.project.model.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserCompositeRequest extends UserRequest {
    @Override
    @NotNull
    @Valid
    public NutritionalValueRequest getDailyGoal() {
        return super.getDailyGoal();
    }

    @NotBlank
    private String mealName;

    @NotNull
    private LocalDate mealDate;

    @NotEmpty
    private List<@NotBlank String> notes;

    private boolean failAfterUser;
}
