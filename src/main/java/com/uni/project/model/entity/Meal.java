package com.uni.project.model.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.AttributeOverride;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PreRemove;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Meal {
    @EqualsAndHashCode.Include
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column
    private Integer id;

    @Column(nullable = false, length = 50)
    private String name;

    @Column
    private LocalDate date;

    @Embedded
    @AttributeOverride(name = "calories", column = @Column(name = "total_calories"))
    @AttributeOverride(name = "proteins", column = @Column(name = "total_proteins"))
    @AttributeOverride(name = "fats", column = @Column(name = "total_fats"))
    @AttributeOverride(name = "carbohydrates", column = @Column(name = "total_carbohydrates"))
    private NutritionalValue totalNutritional;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn
    private User author;

    @ManyToMany(mappedBy = "mealList", fetch = FetchType.LAZY)
    private List<Product> productList;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn
    private Note recipe;

    @PreRemove
    private void unlinkProducts() {
        if (productList == null) {
            return;
        }
        for (Product product : new ArrayList<>(productList)) {
            if (product.getMealList() != null) {
                product.getMealList().remove(this);
            }
        }
        productList.clear();
    }
}
