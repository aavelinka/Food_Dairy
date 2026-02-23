package com.uni.project.model.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class NutritionalValue {
    @EqualsAndHashCode.Include
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column
    private Integer id;

    @Column(nullable = false)
    private Double calories;

    @Column(nullable = false)
    private Double proteins;

    @Column(nullable = false)
    private Double fats;

    @Column(nullable = false)
    private Double carbohydrates;

    @OneToOne(mappedBy = "dailyGoal", fetch = FetchType.EAGER)
    private User owner;

    @OneToOne(mappedBy = "nutritionalValue100g", fetch = FetchType.EAGER)
    private Product product;

    @OneToOne(mappedBy = "totalNutritional", fetch = FetchType.EAGER)
    private Meal meal;
}
