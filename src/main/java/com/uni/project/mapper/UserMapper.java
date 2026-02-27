package com.uni.project.mapper;

import com.uni.project.model.dto.request.UserRequest;
import com.uni.project.model.dto.response.UserResponse;
import com.uni.project.model.entity.Meal;
import com.uni.project.model.entity.NutritionalValue;
import com.uni.project.model.entity.User;
import java.util.List;
import java.util.Objects;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper(componentModel = "spring")
public interface UserMapper {
    @Mapping(target = "dailyGoalId", source = "dailyGoal.id")
    @Mapping(target = "mealIds", source = "mealsPlan", qualifiedByName = "mapMealIds")
    UserResponse toResponse(User user);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "name", source = "userRequest.name")
    @Mapping(target = "password", source = "userRequest.password")
    @Mapping(target = "email", source = "userRequest.email")
    @Mapping(target = "measurements", source = "userRequest.measurements")
    @Mapping(target = "dailyGoal", source = "dailyGoal")
    @Mapping(target = "mealsPlan", source = "meals")
    User fromRequest(UserRequest userRequest, NutritionalValue dailyGoal, List<Meal> meals);

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

}
