package com.uni.project.service.impl;

import com.uni.project.exception.MealException;
import com.uni.project.mapper.MealMapper;
import com.uni.project.model.dto.request.MealRequest;
import com.uni.project.model.dto.response.MealResponse;
import com.uni.project.model.entity.Meal;
import com.uni.project.model.entity.Product;
import com.uni.project.model.entity.User;
import com.uni.project.repository.MealRepository;
import com.uni.project.repository.UserRepository;
import com.uni.project.service.MealService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@AllArgsConstructor
@Transactional(readOnly = true)
public class MealServiceImpl implements MealService {
    private final MealRepository mealRepository;
    private final UserRepository userRepository;
    private final MealMapper mealMapper;
    private static final String MEAL_FAIL_MESSAGE = "Meal is not found by Id";

    @Override
    @Transactional
    public MealResponse mealCreate(MealRequest mealRequest) {
        User author = getAuthor(mealRequest.getAuthorId());
        Meal meal = mealRepository
                .save(mealMapper.fromRequest(mealRequest, author));

        return mealMapper.toResponse(meal);
    }

    @Override
    public MealResponse getMealById(Integer id) {
        Meal meal = mealRepository.findById(id)
                .orElseThrow(() -> new MealException(MEAL_FAIL_MESSAGE));

        return mealMapper.toResponse(meal);
    }

    @Override
    public List<MealResponse> getAllMeals() {
        return mealMapper.toResponses(mealRepository.findAll());
    }

    @Override
    @Transactional
    public MealResponse mealUpdate(Integer id, MealRequest mealRequest) {
        Meal meal = mealRepository.findById(id)
                .orElseThrow(() -> new MealException(MEAL_FAIL_MESSAGE));
        User author = getAuthor(mealRequest.getAuthorId());
        Meal mappedMeal = mealMapper.fromRequest(mealRequest, author);
        meal.setName(mealRequest.getName());
        meal.setDate(mealRequest.getDate());
        meal.setAuthor(author);
        meal.setTotalNutritional(mappedMeal.getTotalNutritional());
        mealRepository.save(meal);

        return mealMapper.toResponse(meal);
    }

    @Override
    @Transactional
    public void mealDelete(Integer id) {
        Meal meal = mealRepository.findById(id)
                .orElseThrow(() -> new MealException(MEAL_FAIL_MESSAGE));

        if (meal.getProductList() != null) {
            for (Product product : new ArrayList<>(meal.getProductList())) {
                if (product.getMealList() != null) {
                    product.getMealList().remove(meal);
                }
            }
            meal.getProductList().clear();
        }

        mealRepository.delete(meal);
    }

    @Override
    public List<MealResponse> getAllMealsByName(String nameSearch) {
        return mealMapper.toResponses(mealRepository.findAllByName(nameSearch));
    }

    @Override
    public List<MealResponse> getAllMealsByAuthorId(Integer authorId) {
        return mealMapper.toResponses(mealRepository.findAllByAuthorId(authorId));
    }

    @Override
    public List<MealResponse> getAllMealsByProductIds(List<Integer> productIds) {
        return mealMapper.toResponses(mealRepository.findAllByProductIds(productIds));
    }

    private User getAuthor(Integer authorId) {
        if (authorId == null) {
            return null;
        }
        return userRepository.findById(authorId)
                .orElseThrow(() -> new MealException("Author not found by Id"));
    }
}
