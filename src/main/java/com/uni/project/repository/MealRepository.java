package com.uni.project.repository;

import com.uni.project.model.entity.Meal;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface MealRepository extends JpaRepository<Meal, Integer> {
    List<Meal> findAllByName(String name);

    @Query("select m from Meal m where m.author.id = :authorId")
    List<Meal> findAllByAuthorId(@Param("authorId") Integer authorId);

    @Query("select m from Meal m where m.totalNutritional.id = :totalNutritionalId")
    List<Meal> findAllByTotalNutritionalId(@Param("totalNutritionalId") Integer totalNutritionalId);

    @Query("select distinct m from Meal m join m.productList p where p.id in :productIds")
    List<Meal> findAllByProductIds(@Param("productIds") List<Integer> productIds);
}
