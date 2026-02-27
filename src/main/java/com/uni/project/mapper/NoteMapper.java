package com.uni.project.mapper;

import com.uni.project.model.dto.request.NoteRequest;
import com.uni.project.model.dto.response.NoteResponse;
import com.uni.project.model.entity.Meal;
import com.uni.project.model.entity.Note;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface NoteMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "meal", source = "meal")
    Note fromRequest(NoteRequest noteRequest, Meal meal);

    @Mapping(target = "mealId", source = "meal.id")
    NoteResponse toResponse(Note note);
}
