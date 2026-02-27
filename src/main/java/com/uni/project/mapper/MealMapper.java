package com.uni.project.mapper;

import com.uni.project.model.dto.request.MealRequest;
import com.uni.project.model.dto.response.MealResponse;
import com.uni.project.model.entity.Meal;
import com.uni.project.model.entity.Note;
import com.uni.project.model.entity.NutritionalValue;
import com.uni.project.model.entity.Product;
import com.uni.project.model.entity.User;
import java.util.List;
import java.util.Objects;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper(componentModel = "spring")
public interface MealMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "name", source = "mealRequest.name")
    @Mapping(target = "date", source = "mealRequest.date")
    @Mapping(target = "author", source = "author")
    @Mapping(target = "totalNutritional", source = "totalNutritional")
    @Mapping(target = "productList", source = "products")
    @Mapping(target = "recipe", source = "recipe")
    Meal fromRequest(MealRequest mealRequest, User author, NutritionalValue totalNutritional,
                     List<Product> products, Note recipe);

    @Mapping(target = "authorId", source = "author.id")
    @Mapping(target = "date", source = "date")
    @Mapping(target = "totalNutritionalId", source = "totalNutritional.id")
    @Mapping(target = "productIds", source = "productList", qualifiedByName = "mapProductIds")
    @Mapping(target = "recipeId", source = "recipe.id")
    MealResponse toResponse(Meal meal);

    @Named("mapProductIds")
    default List<Integer> mapProductIds(List<Product> products) {
        if (products == null || products.isEmpty()) {
            return List.of();
        }
        return products.stream()
                .filter(Objects::nonNull)
                .map(Product::getId)
                .filter(Objects::nonNull)
                .toList();
    }
}
