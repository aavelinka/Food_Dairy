package com.uni.project.service.impl;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.uni.project.mapper.ProductMapper;
import com.uni.project.model.entity.Meal;
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

    @InjectMocks
    private ProductServiceImpl productService;

    @Test
    void productDeleteDeletesMealsContainingProductAndProduct() {
        Product product = new Product();
        product.setId(1);

        Meal firstMeal = new Meal();
        Meal secondMeal = new Meal();
        List<Meal> meals = List.of(firstMeal, secondMeal);

        when(productRepository.findById(1)).thenReturn(Optional.of(product));
        when(mealRepository.findAllByProductIds(List.of(1))).thenReturn(meals);

        productService.productDelete(1);

        verify(mealRepository).deleteAll(meals);
        verify(productRepository).delete(product);
    }

    @Test
    void productDeleteDeletesProductWhenNoRelatedMealsExist() {
        Product product = new Product();
        product.setId(2);

        when(productRepository.findById(2)).thenReturn(Optional.of(product));
        when(mealRepository.findAllByProductIds(List.of(2))).thenReturn(List.of());

        productService.productDelete(2);

        verify(productRepository).delete(product);
    }
}
