package com.uni.project.mapper;

import com.uni.project.model.dto.request.ProductRequest;
import com.uni.project.model.dto.response.ProductResponse;
import com.uni.project.model.entity.Meal;
import com.uni.project.model.entity.Product;
import java.util.List;
import java.util.Objects;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper(componentModel = "spring", uses = NutritionalValueMapper.class)
public interface ProductMapper {
    @Mapping(target = "mealIds", source = "mealList", qualifiedByName = "mapMealIds")
    ProductResponse toResponse(Product product);

    List<ProductResponse> toResponses(List<Product> products);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "mealList", source = "meals")
    Product fromRequest(ProductRequest productRequest, List<Meal> meals);

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
