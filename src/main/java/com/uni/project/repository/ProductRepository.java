package com.uni.project.repository;

import com.uni.project.model.entity.Product;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepository extends JpaRepository<Product, Integer> {
    List<Product> findAllByName(String name);

    @Query("select distinct p from Product p join p.mealList m where m.id = :mealId")
    List<Product> findAllByMealId(@Param("mealId") Integer mealId);
}
