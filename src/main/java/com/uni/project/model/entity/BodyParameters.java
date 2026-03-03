package com.uni.project.model.entity;

import jakarta.persistence.AttributeOverride;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "body_parameters")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class BodyParameters {
    @EqualsAndHashCode.Include
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column
    private Integer id;

    @Column(nullable = false)
    @NotNull
    private LocalDate recordDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @NotNull
    private Sex sex;

    @Column
    @NotNull
    @Positive
    private Double weight;

    @Column
    @NotNull
    @Positive
    private Double height;

    @Column
    @NotNull
    @Positive
    private Integer age;

    @Column
    @PositiveOrZero
    private Double chest;

    @Column
    @PositiveOrZero
    private Double waist;

    @Column
    @PositiveOrZero
    private Double hips;

    @Embedded
    @AttributeOverride(name = "calories", column = @Column(name = "goal_calories"))
    @AttributeOverride(name = "proteins", column = @Column(name = "goal_proteins"))
    @AttributeOverride(name = "fats", column = @Column(name = "goal_fats"))
    @AttributeOverride(name = "carbohydrates", column = @Column(name = "goal_carbohydrates"))
    private NutritionalValue goalNutritional;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User owner;
}
