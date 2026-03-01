package com.uni.project.model.dto.request;

import jakarta.validation.Valid;
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

    @NotNull
    private LocalDate noteDate;

    @NotNull
    private List<String> noteTexts;

    private boolean failAfterUser;
}
