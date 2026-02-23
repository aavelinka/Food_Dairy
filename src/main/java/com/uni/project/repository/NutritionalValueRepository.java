package com.uni.project.repository;

import com.uni.project.model.entity.NutritionalValue;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NutritionalValueRepository extends JpaRepository<NutritionalValue, Integer> {
}
