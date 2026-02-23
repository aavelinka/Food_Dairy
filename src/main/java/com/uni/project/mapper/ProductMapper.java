package com.uni.project.mapper;

import com.uni.project.model.dto.request.ProductRequest;
import com.uni.project.model.dto.response.ProductResponse;
import com.uni.project.model.entity.Meal;
import com.uni.project.model.entity.NutritionalValue;
import com.uni.project.model.entity.Product;
import java.util.List;
import java.util.Objects;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper(componentModel = "spring")
public interface ProductMapper {
    @Mapping(target = "nutritionalValue100gId", source = "nutritionalValue100g.id")
    @Mapping(target = "mealIds", source = "mealList", qualifiedByName = "mapMealIds")
    ProductResponse toResponse(Product product);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "name", source = "productRequest.name")
    @Mapping(target = "nutritionalValue100g", source = "nutritionalValue100g")
    @Mapping(target = "mealList", source = "meals")
    Product fromRequest(ProductRequest productRequest, NutritionalValue nutritionalValue100g,
                        List<Meal> meals);

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
