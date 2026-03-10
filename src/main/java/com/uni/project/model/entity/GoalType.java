package com.uni.project.model.entity;

public enum GoalType {
    WEIGHT_LOSS(0.85),
    MAINTENANCE(1.0),
    WEIGHT_GAIN(1.1);

    private final double calorieCoefficient;

    GoalType(double calorieCoefficient) {
        this.calorieCoefficient = calorieCoefficient;
    }

    public double getCalorieCoefficient() {
        return calorieCoefficient;
    }
}
