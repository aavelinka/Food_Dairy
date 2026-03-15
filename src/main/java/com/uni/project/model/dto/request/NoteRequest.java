package com.uni.project.model.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Request payload for creating or updating a note")
public class NoteRequest {
    @NotNull
    @Positive
    @Schema(description = "Meal id to which the note belongs", example = "1")
    private Integer mealId;

    @NotEmpty
    @Schema(description = "List of note fragments")
    private List<@NotNull @Size(min = 1, max = 255) String> notes;
}
