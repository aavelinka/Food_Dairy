package com.uni.project.service.impl;

import com.uni.project.cache.UserSearchCache;
import com.uni.project.exception.BulkMealCreationException;
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
import com.uni.project.service.MealService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;

@Service
@AllArgsConstructor
@Transactional(readOnly = true)
public class MealServiceImpl implements MealService {
    private final MealRepository mealRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final MealMapper mealMapper;
    private final UserSearchCache userSearchCache;
    private static final String MEAL_FAIL_MESSAGE = "Meal is not found by Id";

    @Override
    @Transactional
    public MealResponse mealCreate(MealRequest mealRequest) {
        Meal meal = mealRepository.save(toMeal(mealRequest));
        userSearchCache.clear();

        return mealMapper.toResponse(meal);
    }

    @Override
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    public List<MealResponse> createBulkNoTx(List<MealRequest> mealRequests, Integer failAfterIndex) {
        return createBulkInternal(mealRequests, failAfterIndex);
    }

    @Override
    @Transactional
    public List<MealResponse> createBulkTx(List<MealRequest> mealRequests, Integer failAfterIndex) {
        return createBulkInternal(mealRequests, failAfterIndex);
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
        Meal mappedMeal = toMeal(mealRequest);
        meal.setName(mealRequest.getName());
        meal.setDate(mealRequest.getDate());
        meal.setAuthor(mappedMeal.getAuthor());
        meal.setTotalNutritional(mappedMeal.getTotalNutritional());
        meal.setProductList(mappedMeal.getProductList());
        mealRepository.save(meal);
        userSearchCache.clear();

        return mealMapper.toResponse(meal);
    }

    @Override
    @Transactional
    public void mealDelete(Integer id) {
        Meal meal = mealRepository.findById(id)
                .orElseThrow(() -> new MealException(MEAL_FAIL_MESSAGE));

        if (meal.getProductList() != null) {
            meal.getProductList().clear();
        }

        mealRepository.delete(meal);
        userSearchCache.clear();
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

    private List<MealResponse> createBulkInternal(List<MealRequest> mealRequests, Integer failAfterIndex) {
        try {
            return IntStream.range(0, mealRequests.size())
                    .mapToObj(index -> saveBulkMeal(
                            mealRequests.get(index),
                            index + 1,
                            failAfterIndex
                    ))
                    .toList();
        } finally {
            userSearchCache.clear();
        }
    }

    private MealResponse saveBulkMeal(MealRequest mealRequest, int processedMealsCount, Integer failAfterIndex) {
        Meal savedMeal = mealRepository.save(toMeal(mealRequest));
        if (shouldFailAfter(processedMealsCount, failAfterIndex)) {
            throw new BulkMealCreationException(
                    "Forced error after saving %d bulk meals".formatted(processedMealsCount)
            );
        }
        return mealMapper.toResponse(savedMeal);
    }

    private Meal toMeal(MealRequest mealRequest) {
        User author = Optional.ofNullable(mealRequest.getAuthorId())
                .map(authorId -> userRepository.findById(authorId)
                        .orElseThrow(() -> new MealException("Author not found by Id")))
                .orElse(null);
        List<Product> products = Optional.ofNullable(mealRequest.getProductIds())
                .filter(productIds -> !productIds.isEmpty())
                .map(productIds -> {
                    List<Product> foundProducts = productRepository.findAllById(productIds);
                    if (foundProducts.size() != productIds.size()) {
                        throw new MealException("Some products not found");
                    }
                    return foundProducts;
                })
                .orElseGet(List::of);
        return mealMapper.fromRequest(mealRequest, author, products);
    }

    private boolean shouldFailAfter(int processedMealsCount, Integer failAfterIndex) {
        return Optional.ofNullable(failAfterIndex)
                .filter(index -> index == processedMealsCount)
                .isPresent();
    }
}
