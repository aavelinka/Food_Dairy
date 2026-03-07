package com.uni.project.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.uni.project.exception.MealException;
import com.uni.project.mapper.MealMapper;
import com.uni.project.model.dto.request.MealRequest;
import com.uni.project.model.dto.response.MealResponse;
import com.uni.project.model.entity.Meal;
import com.uni.project.model.entity.Product;
import com.uni.project.model.entity.User;
import com.uni.project.repository.MealRepository;
import com.uni.project.repository.ProductRepository;
import com.uni.project.repository.UserRepository;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class MealServiceImplTest {
    @Mock
    private MealRepository mealRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private MealMapper mealMapper;

    @InjectMocks
    private MealServiceImpl mealService;

    @Test
    void mealCreateMapsProductsFromRequestIds() {
        MealRequest request = new MealRequest();
        request.setName("Breakfast");
        request.setDate(LocalDate.of(2026, 3, 7));
        request.setAuthorId(10);
        request.setProductIds(List.of(1, 2));

        Product firstProduct = new Product();
        firstProduct.setId(1);
        Product secondProduct = new Product();
        secondProduct.setId(2);
        List<Product> products = List.of(firstProduct, secondProduct);

        User author = new User();
        author.setId(10);

        Meal meal = new Meal();
        meal.setId(15);
        meal.setProductList(new ArrayList<>(products));

        MealResponse response = new MealResponse();
        response.setId(15);
        response.setProductIds(List.of(1, 2));

        when(userRepository.findById(10)).thenReturn(Optional.of(author));
        when(productRepository.findAllById(List.of(1, 2))).thenReturn(products);
        when(mealMapper.fromRequest(request, author, products)).thenReturn(meal);
        when(mealRepository.save(meal)).thenReturn(meal);
        when(mealMapper.toResponse(meal)).thenReturn(response);

        MealResponse actual = mealService.mealCreate(request);

        assertEquals(15, actual.getId());
        assertEquals(List.of(1, 2), actual.getProductIds());
        verify(mealMapper).fromRequest(request, author, products);
    }

    @Test
    void mealCreateThrowsIfSomeProductsAreMissing() {
        MealRequest request = new MealRequest();
        request.setProductIds(List.of(1, 2));

        Product onlyOne = new Product();
        onlyOne.setId(1);

        when(productRepository.findAllById(List.of(1, 2))).thenReturn(List.of(onlyOne));

        assertThrows(MealException.class, () -> mealService.mealCreate(request));
    }

    @Test
    void mealDeleteKeepsProductsAndDeletesOnlyMeal() {
        Product product = new Product();
        product.setId(50);

        Meal meal = new Meal();
        meal.setId(5);
        meal.setProductList(new ArrayList<>(List.of(product)));

        when(mealRepository.findById(5)).thenReturn(Optional.of(meal));

        mealService.mealDelete(5);

        assertEquals(List.of(), meal.getProductList());
        verify(mealRepository).delete(meal);
    }
}
