package com.uni.project.mapper;

import com.uni.project.model.dto.request.ProductRequest;
import com.uni.project.model.dto.response.ProductResponse;
import com.uni.project.model.entity.Product;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = NutritionalValueMapper.class)
public interface ProductMapper {
    ProductResponse toResponse(Product product);

    List<ProductResponse> toResponses(List<Product> products);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "mealList", ignore = true)
    Product fromRequest(ProductRequest productRequest);
}
