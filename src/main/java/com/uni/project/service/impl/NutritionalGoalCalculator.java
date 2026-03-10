package com.uni.project.service.impl;

import com.uni.project.model.entity.BodyParameters;
import com.uni.project.model.entity.GoalType;
import com.uni.project.model.entity.NutritionalValue;
import com.uni.project.model.entity.Sex;
import org.springframework.stereotype.Component;

@Component
public class NutritionalGoalCalculator {
    public NutritionalValue calculate(BodyParameters measurements, GoalType goalType) {
        double baseValues = (measurements.getWeight() * 10
                + measurements.getHeight() * 6.25
                - measurements.getAge() * 5);
        baseValues = (measurements.getSex() == Sex.FEMALE)
                ? (baseValues - 161)
                : (baseValues + 5);
        double adjustedCalories = baseValues * resolveGoalType(goalType).getCalorieCoefficient();

        NutritionalValue nutritionalValue = new NutritionalValue();
        nutritionalValue.setCalories(adjustedCalories);
        nutritionalValue.setProteins(adjustedCalories * 0.25);
        nutritionalValue.setFats(adjustedCalories * 0.2);
        nutritionalValue.setCarbohydrates(adjustedCalories * 0.55);
        return nutritionalValue;
    }

    private GoalType resolveGoalType(GoalType goalType) {
        return goalType == null ? GoalType.MAINTENANCE : goalType;
    }
}
