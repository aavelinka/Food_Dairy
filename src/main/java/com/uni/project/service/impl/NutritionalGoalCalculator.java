package com.uni.project.service.impl;

import com.uni.project.model.entity.BodyParameters;
import com.uni.project.model.entity.GoalType;
import com.uni.project.model.entity.NutritionalValue;
import com.uni.project.model.entity.Sex;
import org.springframework.stereotype.Component;

@Component
public class NutritionalGoalCalculator {
    private static final double PROTEIN_SHARE = 0.25;
    private static final double FAT_SHARE = 0.2;
    private static final double CARBOHYDRATE_SHARE = 0.55;
    private static final double CALORIES_PER_GRAM_PROTEIN = 4.0;
    private static final double CALORIES_PER_GRAM_FAT = 9.0;
    private static final double CALORIES_PER_GRAM_CARBOHYDRATE = 4.0;

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
        nutritionalValue.setProteins((adjustedCalories * PROTEIN_SHARE) / CALORIES_PER_GRAM_PROTEIN);
        nutritionalValue.setFats((adjustedCalories * FAT_SHARE) / CALORIES_PER_GRAM_FAT);
        nutritionalValue.setCarbohydrates(
                (adjustedCalories * CARBOHYDRATE_SHARE) / CALORIES_PER_GRAM_CARBOHYDRATE
        );
        return nutritionalValue;
    }

    private GoalType resolveGoalType(GoalType goalType) {
        return goalType == null ? GoalType.MAINTENANCE : goalType;
    }
}
