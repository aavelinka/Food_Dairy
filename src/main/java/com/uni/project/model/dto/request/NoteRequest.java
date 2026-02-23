package com.uni.project.model.dto.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class NoteRequest {
    @NotNull
    private Integer userId;

    private Integer mealId;

    @NotNull
    private LocalDate date;

    @NotEmpty
    private List<String> notes;
}
