package com.uni.project.mapper;

import com.uni.project.model.dto.request.NoteRequest;
import com.uni.project.model.dto.response.NoteResponse;
import com.uni.project.model.entity.Meal;
import com.uni.project.model.entity.Note;
import com.uni.project.model.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface NoteMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "user", source = "user")
    @Mapping(target = "meal", source = "meal")
    Note fromRequest(NoteRequest noteRequest, User user, Meal meal);

    @Mapping(target = "userId", source = "user.id")
    @Mapping(target = "mealId", source = "meal.id")
    NoteResponse toResponse(Note note);
}
