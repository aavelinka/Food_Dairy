package com.uni.project.model.entity;

import jakarta.persistence.Column;
import jakarta.persistence.AttributeOverride;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Entity
@Table(name = "products")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Product {
    @EqualsAndHashCode.Include
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name = "name", nullable = false, length = 50)
    private String name;

    @Embedded
    @AttributeOverride(name = "calories", column = @Column(name = "nv100_calories"))
    @AttributeOverride(name = "proteins", column = @Column(name = "nv100_proteins"))
    @AttributeOverride(name = "fats", column = @Column(name = "nv100_fats"))
    @AttributeOverride(name = "carbohydrates", column = @Column(name = "nv100_carbohydrates"))
    private NutritionalValue nutritionalValue100g;

    @ManyToMany
    @JoinTable(
            name = "meals_lists",
            joinColumns = @JoinColumn(name = "product_id"),
            inverseJoinColumns = @JoinColumn(name = "meal_id")
    )
    private List<Meal> mealList;
}
