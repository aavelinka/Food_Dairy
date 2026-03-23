package com.uni.project.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.uni.project.cache.UserSearchCache;
import com.uni.project.mapper.ProductMapper;
import com.uni.project.model.dto.request.NutritionalValueRequest;
import com.uni.project.model.dto.request.ProductRequest;
import com.uni.project.model.dto.response.ProductResponse;
import com.uni.project.model.entity.Meal;
import com.uni.project.model.entity.NutritionalValue;
import com.uni.project.model.entity.Product;
import com.uni.project.repository.MealRepository;
import com.uni.project.repository.ProductRepository;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ProductServiceImplTest {
    @Mock
    private ProductRepository productRepository;

    @Mock
    private MealRepository mealRepository;

    @Mock
    private ProductMapper productMapper;

    @Mock
    private UserSearchCache userSearchCache;

    @InjectMocks
    private ProductServiceImpl productService;

    @Test
    void productCreateShouldSaveMappedEntity() {
        ProductRequest request = buildRequest();
        Product mappedProduct = new Product();
        Product savedProduct = new Product();
        ProductResponse expectedResponse = new ProductResponse();

        when(productMapper.fromRequest(request)).thenReturn(mappedProduct);
        when(productRepository.save(mappedProduct)).thenReturn(savedProduct);
        when(productMapper.toResponse(savedProduct)).thenReturn(expectedResponse);

        ProductResponse actualResponse = productService.productCreate(request);

        assertSame(expectedResponse, actualResponse);
    }

    @Test
    void getProductByIdShouldReturnMappedResponse() {
        Product product = new Product();
        ProductResponse expectedResponse = new ProductResponse();

        when(productRepository.findById(1)).thenReturn(Optional.of(product));
        when(productMapper.toResponse(product)).thenReturn(expectedResponse);

        ProductResponse actualResponse = productService.getProductById(1);

        assertSame(expectedResponse, actualResponse);
    }

    @Test
    void getAllProductsShouldReturnMappedResponses() {
        List<Product> products = List.of(new Product());
        List<ProductResponse> expectedResponses = List.of(new ProductResponse());

        when(productRepository.findAll()).thenReturn(products);
        when(productMapper.toResponses(products)).thenReturn(expectedResponses);

        List<ProductResponse> actualResponses = productService.getAllProducts();

        assertEquals(expectedResponses, actualResponses);
    }

    @Test
    void productUpdateShouldUpdateFieldsAndReturnMappedResponse() {
        ProductRequest request = buildRequest();
        Product existingProduct = new Product();
        Product mappedProduct = new Product();
        ProductResponse expectedResponse = new ProductResponse();

        mappedProduct.setNutritionalValue100g(new NutritionalValue(100.0, 10.0, 5.0, 12.0));

        when(productRepository.findById(2)).thenReturn(Optional.of(existingProduct));
        when(productMapper.fromRequest(request)).thenReturn(mappedProduct);
        when(productRepository.save(existingProduct)).thenReturn(existingProduct);
        when(productMapper.toResponse(existingProduct)).thenReturn(expectedResponse);

        ProductResponse actualResponse = productService.productUpdate(2, request);

        assertSame(expectedResponse, actualResponse);
        assertEquals(request.getName(), existingProduct.getName());
        assertSame(mappedProduct.getNutritionalValue100g(), existingProduct.getNutritionalValue100g());
    }

    @Test
    void productDeleteShouldDeleteDependentMealsAndClearCache() {
        Product product = new Product();
        Meal meal = new Meal();
        List<Meal> meals = List.of(meal);

        when(productRepository.findById(3)).thenReturn(Optional.of(product));
        when(mealRepository.findAllByProductIds(List.of(3))).thenReturn(meals);

        productService.productDelete(3);

        verify(mealRepository).deleteAll(meals);
        verify(userSearchCache).clear();
        verify(productRepository).delete(product);
    }

    @Test
    void productDeleteShouldDeleteProductOnlyWhenNoDependentMeals() {
        Product product = new Product();

        when(productRepository.findById(4)).thenReturn(Optional.of(product));
        when(mealRepository.findAllByProductIds(List.of(4))).thenReturn(List.of());

        productService.productDelete(4);

        verify(mealRepository, never()).deleteAll(List.of());
        verify(userSearchCache, never()).clear();
        verify(productRepository).delete(product);
    }

    @Test
    void getAllProductsByNameShouldReturnMappedResponses() {
        List<Product> products = List.of(new Product());
        List<ProductResponse> expectedResponses = List.of(new ProductResponse());

        when(productRepository.findAllByName("Chicken")).thenReturn(products);
        when(productMapper.toResponses(products)).thenReturn(expectedResponses);

        List<ProductResponse> actualResponses = productService.getAllProductsByName("Chicken");

        assertEquals(expectedResponses, actualResponses);
    }

    @Test
    void getAllProductsByMealIdShouldReturnMappedResponses() {
        List<Product> products = List.of(new Product());
        List<ProductResponse> expectedResponses = List.of(new ProductResponse());

        when(productRepository.findAllByMealId(8)).thenReturn(products);
        when(productMapper.toResponses(products)).thenReturn(expectedResponses);

        List<ProductResponse> actualResponses = productService.getAllProductsByMealId(8);

        assertEquals(expectedResponses, actualResponses);
    }

    private ProductRequest buildRequest() {
        return new ProductRequest(
                "Chicken breast",
                new NutritionalValueRequest(120.0, 22.0, 3.0, 0.0)
        );
    }
}
