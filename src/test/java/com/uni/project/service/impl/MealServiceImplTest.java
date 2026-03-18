package com.uni.project.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.ArgumentMatchers.same;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.uni.project.cache.UserSearchCache;
import com.uni.project.exception.BulkMealCreationException;
import com.uni.project.exception.MealException;
import com.uni.project.mapper.MealMapper;
import com.uni.project.model.dto.request.MealRequest;
import com.uni.project.model.dto.request.NutritionalValueRequest;
import com.uni.project.model.dto.response.MealResponse;
import com.uni.project.model.entity.Meal;
import com.uni.project.model.entity.NutritionalValue;
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

    @Mock
    private UserSearchCache userSearchCache;

    @InjectMocks
    private MealServiceImpl mealService;

    @Test
    void mealCreateShouldSaveMealAndClearCache() {
        MealRequest request = buildMealRequest(10, List.of(1, 2));
        User author = buildUser(10);
        List<Product> products = List.of(buildProduct(1), buildProduct(2));
        Meal mappedMeal = new Meal();
        Meal savedMeal = new Meal();
        MealResponse expectedResponse = new MealResponse();

        when(userRepository.findById(10)).thenReturn(Optional.of(author));
        when(productRepository.findAllById(request.getProductIds())).thenReturn(products);
        when(mealMapper.fromRequest(request, author, products)).thenReturn(mappedMeal);
        when(mealRepository.save(mappedMeal)).thenReturn(savedMeal);
        when(mealMapper.toResponse(savedMeal)).thenReturn(expectedResponse);

        MealResponse actualResponse = mealService.mealCreate(request);

        assertSame(expectedResponse, actualResponse);
        verify(userSearchCache).clear();
    }

    @Test
    void mealCreateShouldUseNullAuthorAndEmptyProductsWhenIdsMissing() {
        MealRequest request = buildMealRequest(null, null);
        Meal mappedMeal = new Meal();
        Meal savedMeal = new Meal();
        MealResponse expectedResponse = new MealResponse();

        when(mealMapper.fromRequest(eq(request), isNull(), eq(List.of()))).thenReturn(mappedMeal);
        when(mealRepository.save(mappedMeal)).thenReturn(savedMeal);
        when(mealMapper.toResponse(savedMeal)).thenReturn(expectedResponse);

        MealResponse actualResponse = mealService.mealCreate(request);

        assertSame(expectedResponse, actualResponse);
        verify(userRepository, never()).findById(any());
        verify(productRepository, never()).findAllById(any());
        verify(userSearchCache).clear();
    }

    @Test
    void mealCreateShouldThrowWhenProductsAreMissing() {
        MealRequest request = buildMealRequest(null, List.of(1, 2));
        when(productRepository.findAllById(request.getProductIds())).thenReturn(List.of(buildProduct(1)));

        MealException exception = assertThrows(MealException.class, () -> mealService.mealCreate(request));

        assertEquals("Some products not found", exception.getMessage());
        verify(mealRepository, never()).save(any());
        verify(userSearchCache, never()).clear();
    }

    @Test
    void mealUpdateShouldRefreshMealDataAndClearCache() {
        MealRequest request = buildMealRequest(12, List.of(5));
        User author = buildUser(12);
        List<Product> products = List.of(buildProduct(5));
        Meal existingMeal = new Meal();
        Meal mappedMeal = new Meal();
        NutritionalValue nutritionalValue = new NutritionalValue(600.0, 35.0, 20.0, 50.0);
        MealResponse expectedResponse = new MealResponse();

        existingMeal.setId(1);
        mappedMeal.setAuthor(author);
        mappedMeal.setProductList(products);
        mappedMeal.setTotalNutritional(nutritionalValue);

        when(mealRepository.findById(1)).thenReturn(Optional.of(existingMeal));
        when(userRepository.findById(12)).thenReturn(Optional.of(author));
        when(productRepository.findAllById(request.getProductIds())).thenReturn(products);
        when(mealMapper.fromRequest(request, author, products)).thenReturn(mappedMeal);
        when(mealRepository.save(existingMeal)).thenReturn(existingMeal);
        when(mealMapper.toResponse(existingMeal)).thenReturn(expectedResponse);

        MealResponse actualResponse = mealService.mealUpdate(1, request);

        assertSame(expectedResponse, actualResponse);
        assertEquals(request.getName(), existingMeal.getName());
        assertEquals(request.getDate(), existingMeal.getDate());
        assertSame(author, existingMeal.getAuthor());
        assertSame(products, existingMeal.getProductList());
        assertSame(nutritionalValue, existingMeal.getTotalNutritional());
        verify(userSearchCache).clear();
    }

    @Test
    void mealDeleteShouldClearProductsAndDeleteMeal() {
        Meal meal = new Meal();
        meal.setId(3);
        meal.setProductList(new ArrayList<>(List.of(buildProduct(1), buildProduct(2))));

        when(mealRepository.findById(3)).thenReturn(Optional.of(meal));

        mealService.mealDelete(3);

        assertTrue(meal.getProductList().isEmpty());
        verify(mealRepository).delete(meal);
        verify(userSearchCache).clear();
    }

    @Test
    void mealDeleteShouldDeleteMealWhenProductListIsNull() {
        Meal meal = new Meal();
        meal.setId(4);

        when(mealRepository.findById(4)).thenReturn(Optional.of(meal));

        mealService.mealDelete(4);

        verify(mealRepository).delete(meal);
        verify(userSearchCache).clear();
    }

    @Test
    void createBulkNoTxShouldSaveAllMealsAndReturnResponses() {
        MealRequest firstRequest = buildMealRequest(null, null);
        MealRequest secondRequest = buildMealRequest(null, List.of());
        Meal firstMappedMeal = new Meal();
        Meal secondMappedMeal = new Meal();
        Meal firstSavedMeal = new Meal();
        Meal secondSavedMeal = new Meal();
        MealResponse firstResponse = new MealResponse();
        MealResponse secondResponse = new MealResponse();

        when(mealMapper.fromRequest(eq(firstRequest), isNull(), eq(List.of()))).thenReturn(firstMappedMeal);
        when(mealMapper.fromRequest(eq(secondRequest), isNull(), eq(List.of()))).thenReturn(secondMappedMeal);
        when(mealRepository.save(same(firstMappedMeal))).thenReturn(firstSavedMeal);
        when(mealRepository.save(same(secondMappedMeal))).thenReturn(secondSavedMeal);
        when(mealMapper.toResponse(same(firstSavedMeal))).thenReturn(firstResponse);
        when(mealMapper.toResponse(same(secondSavedMeal))).thenReturn(secondResponse);

        List<MealResponse> actualResponses = mealService.createBulkNoTx(List.of(firstRequest, secondRequest), null);

        assertEquals(List.of(firstResponse, secondResponse), actualResponses);
        verify(userSearchCache).clear();
    }

    @Test
    void createBulkTxShouldSaveAllMealsAndReturnResponses() {
        MealRequest firstRequest = buildMealRequest(null, null);
        MealRequest secondRequest = buildMealRequest(null, List.of());
        Meal firstMappedMeal = new Meal();
        Meal secondMappedMeal = new Meal();
        Meal firstSavedMeal = new Meal();
        Meal secondSavedMeal = new Meal();
        MealResponse firstResponse = new MealResponse();
        MealResponse secondResponse = new MealResponse();

        when(mealMapper.fromRequest(eq(firstRequest), isNull(), eq(List.of()))).thenReturn(firstMappedMeal);
        when(mealMapper.fromRequest(eq(secondRequest), isNull(), eq(List.of()))).thenReturn(secondMappedMeal);
        when(mealRepository.save(same(firstMappedMeal))).thenReturn(firstSavedMeal);
        when(mealRepository.save(same(secondMappedMeal))).thenReturn(secondSavedMeal);
        when(mealMapper.toResponse(same(firstSavedMeal))).thenReturn(firstResponse);
        when(mealMapper.toResponse(same(secondSavedMeal))).thenReturn(secondResponse);

        List<MealResponse> actualResponses = mealService.createBulkTx(List.of(firstRequest, secondRequest), null);

        assertEquals(List.of(firstResponse, secondResponse), actualResponses);
        verify(userSearchCache).clear();
    }

    @Test
    void createBulkTxShouldThrowAfterRequestedIndexAndClearCache() {
        MealRequest firstRequest = buildMealRequest(null, null);
        MealRequest secondRequest = buildMealRequest(null, null);
        MealRequest thirdRequest = buildMealRequest(null, null);
        List<MealRequest> requests = List.of(firstRequest, secondRequest, thirdRequest);
        Meal firstMappedMeal = new Meal();
        Meal secondMappedMeal = new Meal();
        Meal firstSavedMeal = new Meal();
        Meal secondSavedMeal = new Meal();
        MealResponse firstResponse = new MealResponse();

        when(mealMapper.fromRequest(eq(firstRequest), isNull(), eq(List.of()))).thenReturn(firstMappedMeal);
        when(mealMapper.fromRequest(eq(secondRequest), isNull(), eq(List.of()))).thenReturn(secondMappedMeal);
        when(mealRepository.save(same(firstMappedMeal))).thenReturn(firstSavedMeal);
        when(mealRepository.save(same(secondMappedMeal))).thenReturn(secondSavedMeal);
        when(mealMapper.toResponse(same(firstSavedMeal))).thenReturn(firstResponse);

        BulkMealCreationException exception = assertThrows(
                BulkMealCreationException.class,
                () -> mealService.createBulkTx(requests, 2)
        );

        assertEquals("Forced error after saving 2 bulk meals", exception.getMessage());
        verify(mealRepository).save(same(firstMappedMeal));
        verify(mealRepository).save(same(secondMappedMeal));
        verify(mealMapper, never()).fromRequest(eq(thirdRequest), isNull(), eq(List.of()));
        verify(mealMapper, never()).toResponse(same(secondSavedMeal));
        verify(userSearchCache).clear();
    }

    @Test
    void mealCreateShouldThrowWhenAuthorDoesNotExist() {
        MealRequest request = buildMealRequest(42, List.of());
        when(userRepository.findById(42)).thenReturn(Optional.empty());

        MealException exception = assertThrows(MealException.class, () -> mealService.mealCreate(request));

        assertEquals("Author not found by Id", exception.getMessage());
        verify(productRepository, never()).findAllById(any());
        verify(mealRepository, never()).save(any());
    }

    @Test
    void getMealByIdShouldReturnMappedResponse() {
        Meal meal = new Meal();
        MealResponse expectedResponse = new MealResponse();

        when(mealRepository.findById(11)).thenReturn(Optional.of(meal));
        when(mealMapper.toResponse(meal)).thenReturn(expectedResponse);

        MealResponse actualResponse = mealService.getMealById(11);

        assertSame(expectedResponse, actualResponse);
    }

    @Test
    void getAllMealsShouldReturnMappedResponses() {
        List<Meal> meals = List.of(new Meal(), new Meal());
        List<MealResponse> expectedResponses = List.of(new MealResponse(), new MealResponse());

        when(mealRepository.findAll()).thenReturn(meals);
        when(mealMapper.toResponses(meals)).thenReturn(expectedResponses);

        List<MealResponse> actualResponses = mealService.getAllMeals();

        assertEquals(expectedResponses, actualResponses);
    }

    @Test
    void getAllMealsByNameShouldReturnMappedResponses() {
        List<Meal> meals = List.of(new Meal());
        List<MealResponse> expectedResponses = List.of(new MealResponse());

        when(mealRepository.findAllByName("Lunch")).thenReturn(meals);
        when(mealMapper.toResponses(meals)).thenReturn(expectedResponses);

        List<MealResponse> actualResponses = mealService.getAllMealsByName("Lunch");

        assertEquals(expectedResponses, actualResponses);
    }

    @Test
    void getAllMealsByAuthorIdShouldReturnMappedResponses() {
        List<Meal> meals = List.of(new Meal());
        List<MealResponse> expectedResponses = List.of(new MealResponse());

        when(mealRepository.findAllByAuthorId(5)).thenReturn(meals);
        when(mealMapper.toResponses(meals)).thenReturn(expectedResponses);

        List<MealResponse> actualResponses = mealService.getAllMealsByAuthorId(5);

        assertEquals(expectedResponses, actualResponses);
    }

    @Test
    void getAllMealsByProductIdsShouldReturnMappedResponses() {
        List<Integer> productIds = List.of(1, 2);
        List<Meal> meals = List.of(new Meal());
        List<MealResponse> expectedResponses = List.of(new MealResponse());

        when(mealRepository.findAllByProductIds(productIds)).thenReturn(meals);
        when(mealMapper.toResponses(meals)).thenReturn(expectedResponses);

        List<MealResponse> actualResponses = mealService.getAllMealsByProductIds(productIds);

        assertEquals(expectedResponses, actualResponses);
    }

    private MealRequest buildMealRequest(Integer authorId, List<Integer> productIds) {
        return new MealRequest(
                "Lunch",
                LocalDate.of(2026, 3, 18),
                new NutritionalValueRequest(500.0, 30.0, 15.0, 55.0),
                authorId,
                productIds
        );
    }

    private User buildUser(Integer id) {
        User user = new User();
        user.setId(id);
        return user;
    }

    private Product buildProduct(Integer id) {
        Product product = new Product();
        product.setId(id);
        return product;
    }
}
