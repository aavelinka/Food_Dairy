package com.uni.project.mapper;

import com.uni.project.model.dto.request.UserRequest;
import com.uni.project.model.dto.response.UserResponse;
import com.uni.project.model.entity.BodyParameters;
import com.uni.project.model.entity.Meal;
import com.uni.project.model.entity.User;
import java.util.List;
import java.util.Objects;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper(componentModel = "spring", uses = NutritionalValueMapper.class)
public interface UserMapper {
    @Mapping(target = "mealIds", source = "mealsPlan", qualifiedByName = "mapMealIds")
    @Mapping(target = "bodyParametersIds", source = "bodyParametersHistory", qualifiedByName = "mapBodyParametersIds")
    UserResponse toResponse(User user);

    List<UserResponse> toResponses(List<User> users);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "mealsPlan", ignore = true)
    @Mapping(target = "bodyParametersHistory", ignore = true)
    User fromRequest(UserRequest userRequest);

    @Named("mapMealIds")
    default List<Integer> mapMealIds(List<Meal> meals) {
        if (meals == null || meals.isEmpty()) {
            return List.of();
        }
        return meals.stream()
                .filter(Objects::nonNull)
                .map(Meal::getId)
                .filter(Objects::nonNull)
                .toList();
    }

    @Named("mapBodyParametersIds")
    default List<Integer> mapBodyParametersIds(List<BodyParameters> bodyParametersHistory) {
        if (bodyParametersHistory == null || bodyParametersHistory.isEmpty()) {
            return List.of();
        }
        return bodyParametersHistory.stream()
                .filter(Objects::nonNull)
                .map(BodyParameters::getId)
                .filter(Objects::nonNull)
                .toList();
    }

}
