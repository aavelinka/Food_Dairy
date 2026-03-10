package com.uni.project.service.impl;

import com.uni.project.cache.UserSearchCache;
import com.uni.project.exception.ProductException;
import com.uni.project.mapper.ProductMapper;
import com.uni.project.model.dto.request.ProductRequest;
import com.uni.project.model.dto.response.ProductResponse;
import com.uni.project.model.entity.Meal;
import com.uni.project.model.entity.Product;
import com.uni.project.repository.MealRepository;
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
    private final MealRepository mealRepository;
    private final ProductMapper productMapper;
    private final UserSearchCache userSearchCache;
    private static final String PRODUCT_FAIL_MESSAGE = "Product not found by Id";

    @Override
    @Transactional
    public ProductResponse productCreate(ProductRequest productRequest) {
        Product product = productRepository
                .save(productMapper.fromRequest(productRequest));

        return productMapper.toResponse(product);
    }

    @Override
    public ProductResponse getProductById(Integer id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ProductException(PRODUCT_FAIL_MESSAGE));

        return productMapper.toResponse(product);
    }

    @Override
    public List<ProductResponse> getAllProducts() {
        return productMapper.toResponses(productRepository.findAll());
    }

    @Override
    @Transactional
    public ProductResponse productUpdate(Integer id, ProductRequest productRequest) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ProductException(PRODUCT_FAIL_MESSAGE));
        Product mappedProduct = productMapper.fromRequest(productRequest);
        product.setName(productRequest.getName());
        product.setNutritionalValue100g(mappedProduct.getNutritionalValue100g());
        productRepository.save(product);

        return productMapper.toResponse(product);
    }

    @Override
    @Transactional
    public void productDelete(Integer id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ProductException(PRODUCT_FAIL_MESSAGE));

        List<Meal> mealsWithProduct = mealRepository.findAllByProductIds(List.of(id));
        if (!mealsWithProduct.isEmpty()) {
            mealRepository.deleteAll(mealsWithProduct);
            userSearchCache.clear();
        }
        productRepository.delete(product);
    }

    @Override
    public List<ProductResponse> getAllProductsByName(String nameSearch) {
        return productMapper.toResponses(productRepository.findAllByName(nameSearch));
    }

    @Override
    public List<ProductResponse> getAllProductsByMealId(Integer mealId) {
        return productMapper.toResponses(productRepository.findAllByMealId(mealId));
    }
}
