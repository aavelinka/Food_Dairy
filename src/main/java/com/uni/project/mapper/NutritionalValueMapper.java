package com.uni.project.mapper;

import com.uni.project.model.dto.request.NutritionalValueRequest;
import com.uni.project.model.dto.response.NutritionalValueResponse;
import com.uni.project.model.entity.Meal;
import com.uni.project.model.entity.NutritionalValue;
import com.uni.project.model.entity.Product;
import com.uni.project.model.entity.User;
import java.util.List;
import java.util.Objects;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper(componentModel = "spring")
public interface NutritionalValueMapper {
    @Mapping(target = "ownerId", source = "owners", qualifiedByName = "mapOwnerId")
    @Mapping(target = "productId", source = "product.id")
    @Mapping(target = "mealId", source = "meal.id")
    NutritionalValueResponse toResponse(NutritionalValue nutritionalValue);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "owners", ignore = true)
    @Mapping(target = "product", source = "product")
    @Mapping(target = "meal", source = "meal")
    NutritionalValue fromRequest(NutritionalValueRequest nutritionalValueRequest,
                                 Product product, Meal meal);

    @Named("mapOwnerId")
    default Integer mapOwnerId(List<User> owners) {
        if (owners == null || owners.isEmpty()) {
            return null;
        }
        return owners.stream()
                .filter(Objects::nonNull)
                .map(User::getId)
                .filter(Objects::nonNull)
                .findFirst()
                .orElse(null);
    }
}
