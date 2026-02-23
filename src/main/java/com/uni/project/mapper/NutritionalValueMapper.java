package com.uni.project.mapper;

import com.uni.project.model.dto.request.NutritionalValueRequest;
import com.uni.project.model.dto.response.NutritionalValueResponse;
import com.uni.project.model.entity.Meal;
import com.uni.project.model.entity.NutritionalValue;
import com.uni.project.model.entity.Product;
import com.uni.project.model.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface NutritionalValueMapper {
    @Mapping(target = "ownerId", source = "owner.id")
    @Mapping(target = "productId", source = "product.id")
    @Mapping(target = "mealId", source = "meal.id")
    NutritionalValueResponse toResponse(NutritionalValue nutritionalValue);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "owner", source = "owner")
    @Mapping(target = "product", source = "product")
    @Mapping(target = "meal", source = "meal")
    NutritionalValue fromRequest(NutritionalValueRequest nutritionalValueRequest, User owner,
                                 Product product, Meal meal);
}
