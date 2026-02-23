package com.uni.project.service;

import com.uni.project.model.dto.request.ProductRequest;
import com.uni.project.model.dto.response.ProductResponse;

import java.util.List;

public interface ProductService {
    ProductResponse productCreate(ProductRequest productRequest);

    ProductResponse getProductById(Integer id);

    List<ProductResponse> getAllProducts();

    ProductResponse productUpdate(Integer id, ProductRequest productRequest);

    void productDelete(Integer id);

    List<ProductResponse> getAllProductsByName(String nameSearch);

    List<ProductResponse> getAllProductsByMealId(Integer mealId);
}
