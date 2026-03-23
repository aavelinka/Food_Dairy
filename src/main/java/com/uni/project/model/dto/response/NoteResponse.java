package com.uni.project.model.dto.response;

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
@Schema(description = "Note response")
public class NoteResponse {
    @Schema(description = "Note id", example = "1")
    private Integer id;

    @Schema(description = "Meal id", example = "3")
    private Integer mealId;

    @Schema(description = "List of note fragments")
    private List<String> notes;
}
