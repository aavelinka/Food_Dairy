package com.uni.project.service;

import com.uni.project.model.dto.request.MealRequest;
import com.uni.project.model.dto.response.MealResponse;

import java.util.List;

public interface MealService {
    MealResponse mealCreate(MealRequest mealRequest);

    MealResponse getMealById(Integer id);

    List<MealResponse> getAllMeals();

    MealResponse mealUpdate(Integer id, MealRequest mealRequest);

    void mealDelete(Integer id);

    List<MealResponse> getAllMealsByName(String nameSearch);

    List<MealResponse> getAllMealsByAuthorId(Integer authorId);

    List<MealResponse> getAllMealsByNutritionalValueId(Integer nutritionalValueId);

    List<MealResponse> getAllMealsByProductIds(List<Integer> productIds);
}
