package com.uni.project.model.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
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
@Schema(description = "Composite request for creating a user together with meal and notes")
public class UserCompositeRequest extends UserRequest {
    @NotBlank
    @Size(max = 50)
    @Schema(description = "Meal name", example = "Breakfast")
    private String mealName;

    @NotNull
    @Schema(description = "Meal date", example = "2026-03-12")
    private LocalDate mealDate;

    @NotEmpty
    @Schema(description = "Recipe or meal notes")
    private List<@NotBlank @Size(max = 255) String> notes;

    @Schema(description = "Force failure after user creation to demonstrate transaction behavior")
    private boolean failAfterUser;
}
