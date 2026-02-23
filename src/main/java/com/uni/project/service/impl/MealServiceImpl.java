package com.uni.project.service.impl;

import com.uni.project.exception.MealException;
import com.uni.project.mapper.MealMapper;
import com.uni.project.model.dto.request.MealRequest;
import com.uni.project.model.dto.response.MealResponse;
import com.uni.project.model.entity.Meal;
import com.uni.project.model.entity.Note;
import com.uni.project.model.entity.NutritionalValue;
import com.uni.project.model.entity.Product;
import com.uni.project.model.entity.User;
import com.uni.project.repository.MealRepository;
import com.uni.project.repository.NoteRepository;
import com.uni.project.repository.NutritionalValueRepository;
import com.uni.project.repository.ProductRepository;
import com.uni.project.repository.UserRepository;
import com.uni.project.service.MealService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@AllArgsConstructor
@Transactional(readOnly = true)
public class MealServiceImpl implements MealService {
    private final MealRepository mealRepository;
    private final UserRepository userRepository;
    private final NutritionalValueRepository nutritionalValueRepository;
    private final ProductRepository productRepository;
    private final NoteRepository noteRepository;

    private final MealMapper mealMapper;

    @Override
    @Transactional
    public MealResponse mealCreate(MealRequest mealRequest) {
        User author = getAuthor(mealRequest.getAuthorId());
        NutritionalValue totalNutritional = getTotalNutritional(mealRequest.getTotalNutritionalId());
        List<Product> products = getProducts(mealRequest.getProductIds());
        Note recipe = getRecipe(mealRequest.getRecipeId());
        Meal meal = mealRepository
                .save(mealMapper.fromRequest(mealRequest, author, totalNutritional, products, recipe));

        return mealMapper.toResponse(meal);
    }

    @Override
    public MealResponse getMealById(Integer id) {
        Meal meal = mealRepository.findById(id)
                .orElseThrow(() -> new MealException("Meal not found bu Id"));

        return mealMapper.toResponse(meal);
    }

    @Override
    public List<MealResponse> getAllMeals() {
        return toResponses(mealRepository.findAll());
    }

    @Override
    @Transactional
    public MealResponse mealUpdate(Integer id, MealRequest mealRequest) {
        Meal meal = mealRepository.findById(id)
                .orElseThrow(() -> new MealException("Meal not found by Id"));
        User author = getAuthor(mealRequest.getAuthorId());
        NutritionalValue totalNutritional = getTotalNutritional(mealRequest.getTotalNutritionalId());
        List<Product> products = getProducts(mealRequest.getProductIds());
        Note recipe = getRecipe(mealRequest.getRecipeId());
        meal.setName(mealRequest.getName());
        meal.setAuthor(author);
        meal.setProductList(products);
        meal.setTotalNutritional(totalNutritional);
        meal.setRecipe(recipe);
        mealRepository.save(meal);

        return mealMapper.toResponse(meal);
    }

    @Override
    @Transactional
    public void mealDelete(Integer id) {
        mealRepository.deleteById(id);
    }

    @Override
    public List<MealResponse> getAllMealsByName(String nameSearch) {
        return toResponses(mealRepository.findAllByName(nameSearch));
    }

    @Override
    public List<MealResponse> getAllMealsByAuthorId(Integer authorId) {
        return toResponses(mealRepository.findAllByAuthorId(authorId));
    }

    @Override
    public List<MealResponse> getAllMealsByNutritionalValueId(Integer nutritionalValueId) {
        return toResponses(mealRepository.findAllByTotalNutritionalId(nutritionalValueId));
    }

    @Override
    public List<MealResponse> getAllMealsByProductIds(List<Integer> productIds) {
        return toResponses(mealRepository.findAllByProductIds(productIds));
    }

    private List<MealResponse> toResponses(List<Meal> meals) {
        return meals.stream()
                .map(mealMapper::toResponse)
                .toList();
    }

    private User getAuthor(Integer authorId) {
        if (authorId == null) {
            return null;
        }
        return userRepository.findById(authorId)
                .orElseThrow(() -> new MealException("Author not found by Id"));
    }

    private NutritionalValue getTotalNutritional(Integer totalNutritionalId) {
        if (totalNutritionalId == null) {
            return null;
        }
        return nutritionalValueRepository.findById(totalNutritionalId)
                .orElseThrow(() -> new MealException("Nutritional Value not found by Id"));
    }

    private Note getRecipe(Integer recipeId) {
        if (recipeId == null) {
            return null;
        }
        return noteRepository.findById(recipeId)
                .orElseThrow(() -> new MealException("Recipe not found by Id"));
    }

    private List<Product> getProducts(List<Integer> productIds) {
        if (productIds == null || productIds.isEmpty()) {
            return List.of();
        }
        List<Product> products = productRepository.findAllById(productIds);
        if (products.size() != productIds.size()) {
            throw new MealException("Some products not found");
        }
        return products;
    }
}
