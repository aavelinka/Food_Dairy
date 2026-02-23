package com.uni.project.model.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BodyParameters {
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
}
