package com.uni.project.service.impl;

import com.uni.project.exception.ProductException;
import com.uni.project.mapper.ProductMapper;
import com.uni.project.model.dto.request.ProductRequest;
import com.uni.project.model.dto.response.ProductResponse;
import com.uni.project.model.entity.Meal;
import com.uni.project.model.entity.NutritionalValue;
import com.uni.project.model.entity.Product;
import com.uni.project.repository.MealRepository;
import com.uni.project.repository.NutritionalValueRepository;
import com.uni.project.repository.ProductRepository;
import com.uni.project.service.ProductService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@AllArgsConstructor
@Transactional(readOnly = true)
public class ProductServiceImpl implements ProductService {
    private final ProductRepository productRepository;
    private final NutritionalValueRepository nutritionalValueRepository;
    private final MealRepository mealRepository;

    private final ProductMapper productMapper;

    @Override
    @Transactional
    public ProductResponse productCreate(ProductRequest productRequest) {
        NutritionalValue nutritionalValue100g = getNutritionalValue(productRequest.getNutritionalValue100gId());
        List<Meal> meals = getMeals(productRequest.getMealIds());
        Product product = productRepository
                .save(productMapper.fromRequest(productRequest, nutritionalValue100g, meals));

        return productMapper.toResponse(product);
    }

    @Override
    public ProductResponse getProductById(Integer id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ProductException("Product not found by Id"));

        return productMapper.toResponse(product);
    }

    @Override
    public List<ProductResponse> getAllProducts() {
        return toResponses(productRepository.findAll());
    }

    @Override
    @Transactional
    public ProductResponse productUpdate(Integer id, ProductRequest productRequest) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ProductException("Product not found by Id"));
        NutritionalValue nutritionalValue100g = getNutritionalValue(productRequest.getNutritionalValue100gId());
        List<Meal> meals = getMeals(productRequest.getMealIds());
        product.setName(productRequest.getName());
        product.setNutritionalValue100g(nutritionalValue100g);
        product.setMealList(meals);
        productRepository.save(product);

        return productMapper.toResponse(product);
    }

    @Override
    @Transactional
    public void productDelete(Integer id) {
        productRepository.deleteById(id);
    }

    @Override
    public List<ProductResponse> getAllProductsByName(String nameSearch) {
        return toResponses(productRepository.findAllByName(nameSearch));
    }

    @Override
    public List<ProductResponse> getAllProductsByMealId(Integer mealId) {
        return toResponses(productRepository.findAllByMealId(mealId));
    }

    private List<ProductResponse> toResponses(List<Product> products) {
        return products.stream()
                .map(productMapper::toResponse)
                .toList();
    }

    private NutritionalValue getNutritionalValue(Integer nutritionalValueId) {
        if (nutritionalValueId == null) {
            return null;
        }
        return nutritionalValueRepository.findById(nutritionalValueId)
                .orElseThrow(() -> new ProductException("Nutritional Value not found by Id"));
    }

    private List<Meal> getMeals(List<Integer> mealIds) {
        if (mealIds == null || mealIds.isEmpty()) {
            return List.of();
        }
        List<Meal> meals = mealRepository.findAllById(mealIds);
        if (meals.size() != mealIds.size()) {
            throw new ProductException("Some meals not found");
        }
        return meals;
    }
}
