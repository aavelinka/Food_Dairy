package com.uni.project.model.dto.response;

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
public class NoteResponse {
    private Integer id;

    private Integer userId;

    private Integer mealId;

    private LocalDate date;

    private List<String> notes;
}
