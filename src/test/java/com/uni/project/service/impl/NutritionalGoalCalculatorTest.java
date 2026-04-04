package com.uni.project.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.uni.project.model.entity.BodyParameters;
import com.uni.project.model.entity.GoalType;
import com.uni.project.model.entity.NutritionalValue;
import com.uni.project.model.entity.Sex;
import org.junit.jupiter.api.Test;

class NutritionalGoalCalculatorTest {
    private static final double DELTA = 0.0001;

    private final NutritionalGoalCalculator calculator = new NutritionalGoalCalculator();

    @Test
    void calculateShouldUseMaintenanceWhenGoalTypeIsNull() {
        BodyParameters measurements = buildMeasurements(70.0, 180.0, 25, Sex.MALE);

        NutritionalValue result = calculator.calculate(measurements, null);

        assertEquals(1705.0, result.getCalories(), DELTA);
        assertEquals(106.5625, result.getProteins(), DELTA);
        assertEquals(37.8888888889, result.getFats(), DELTA);
        assertEquals(234.4375, result.getCarbohydrates(), DELTA);
    }

    @Test
    void calculateShouldApplyGoalCoefficientForFemaleWeightLoss() {
        BodyParameters measurements = buildMeasurements(60.0, 165.0, 30, Sex.FEMALE);

        NutritionalValue result = calculator.calculate(measurements, GoalType.WEIGHT_LOSS);

        assertEquals(1122.2125, result.getCalories(), DELTA);
        assertEquals(70.13828125, result.getProteins(), DELTA);
        assertEquals(24.9380555556, result.getFats(), DELTA);
        assertEquals(154.30421875, result.getCarbohydrates(), DELTA);
    }

    @Test
    void calculateShouldKeepCaloriesBalancedAcrossMacros() {
        BodyParameters measurements = buildMeasurements(82.0, 176.0, 31, Sex.MALE);

        NutritionalValue result = calculator.calculate(measurements, GoalType.WEIGHT_GAIN);

        double caloriesFromMacros = result.getProteins() * 4
                + result.getFats() * 9
                + result.getCarbohydrates() * 4;

        assertEquals(result.getCalories(), caloriesFromMacros, DELTA);
    }

    private BodyParameters buildMeasurements(Double weight, Double height, Integer age, Sex sex) {
        BodyParameters measurements = new BodyParameters();
        measurements.setWeight(weight);
        measurements.setHeight(height);
        measurements.setAge(age);
        measurements.setSex(sex);
        return measurements;
    }
}
