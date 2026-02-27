package com.uni.project.service.impl;

import com.uni.project.exception.NutritionalValueException;
import com.uni.project.mapper.NutritionalValueMapper;
import com.uni.project.model.dto.request.NutritionalValueRequest;
import com.uni.project.model.dto.response.NutritionalValueResponse;
import com.uni.project.model.entity.Meal;
import com.uni.project.model.entity.NutritionalValue;
import com.uni.project.model.entity.Product;
import com.uni.project.model.entity.User;
import com.uni.project.repository.MealRepository;
import com.uni.project.repository.NutritionalValueRepository;
import com.uni.project.repository.ProductRepository;
import com.uni.project.repository.UserRepository;
import com.uni.project.service.NutritionalValueService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@AllArgsConstructor
@Transactional(readOnly = true)
public class NutritionalValueServiceImpl implements NutritionalValueService {
    private final NutritionalValueRepository nutritionalValueRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final MealRepository mealRepository;

    private final NutritionalValueMapper nutritionalValueMapper;

    @Override
    @Transactional
    public NutritionalValueResponse nutritionalValueCreate(NutritionalValueRequest nutritionalValueRequest) {
        User owner = getOwner(nutritionalValueRequest.getOwnerId());
        Product product = getProduct(nutritionalValueRequest.getProductId());
        Meal meal = getMeal(nutritionalValueRequest.getMealId());
        NutritionalValue nutritionalValue = nutritionalValueRepository
                .save(nutritionalValueMapper.fromRequest(nutritionalValueRequest, product, meal));
        assignDailyGoalIfRequested(owner, nutritionalValue);

        return nutritionalValueMapper.toResponse(nutritionalValue);
    }

    @Override
    public NutritionalValueResponse getNutritionalValueById(Integer id) {
        NutritionalValue nutritionalValue = nutritionalValueRepository.findById(id)
                .orElseThrow(() -> new NutritionalValueException("Nutritional Value not found by Id"));

        return nutritionalValueMapper.toResponse(nutritionalValue);
    }

    @Override
    public List<NutritionalValueResponse> getAllNutritionalValues() {
        return toResponses(nutritionalValueRepository.findAll());
    }

    @Override
    @Transactional
    public NutritionalValueResponse nutritionalValueUpdate(Integer id,
                                                           NutritionalValueRequest nutritionalValueRequest) {
        NutritionalValue nutritionalValue = nutritionalValueRepository.findById(id)
                .orElseThrow(() -> new NutritionalValueException("Nutritional Value not found by Id"));
        User owner = getOwner(nutritionalValueRequest.getOwnerId());
        Product product = getProduct(nutritionalValueRequest.getProductId());
        Meal meal = getMeal(nutritionalValueRequest.getMealId());
        nutritionalValue.setCalories(nutritionalValueRequest.getCalories());
        nutritionalValue.setProteins(nutritionalValueRequest.getProteins());
        nutritionalValue.setFats(nutritionalValueRequest.getFats());
        nutritionalValue.setCarbohydrates(nutritionalValueRequest.getCarbohydrates());
        nutritionalValue.setProduct(product);
        nutritionalValue.setMeal(meal);
        nutritionalValueRepository.save(nutritionalValue);
        assignDailyGoalIfRequested(owner, nutritionalValue);

        return nutritionalValueMapper.toResponse(nutritionalValue);
    }

    @Override
    @Transactional
    public void nutritionalValueDelete(Integer id) {
        nutritionalValueRepository.deleteById(id);
    }

    private List<NutritionalValueResponse> toResponses(List<NutritionalValue> values) {
        return values.stream()
                .map(nutritionalValueMapper::toResponse)
                .toList();
    }

    private User getOwner(Integer ownerId) {
        if (ownerId == null) {
            return null;
        }
        return userRepository.findById(ownerId)
                .orElseThrow(() -> new NutritionalValueException("Owner not found by Id"));
    }

    private Product getProduct(Integer productId) {
        if (productId == null) {
            return null;
        }
        return productRepository.findById(productId)
                .orElseThrow(() -> new NutritionalValueException("Product not found by Id"));
    }

    private Meal getMeal(Integer mealId) {
        if (mealId == null) {
            return null;
        }
        return mealRepository.findById(mealId)
                .orElseThrow(() -> new NutritionalValueException("Meal not found by Id"));
    }

    private void assignDailyGoalIfRequested(User owner, NutritionalValue nutritionalValue) {
        if (owner == null) {
            return;
        }
        owner.setDailyGoal(nutritionalValue);
        userRepository.save(owner);
    }
}
