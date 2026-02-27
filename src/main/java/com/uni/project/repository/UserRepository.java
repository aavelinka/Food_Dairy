package com.uni.project.repository;

import com.uni.project.model.entity.User;
import com.uni.project.model.entity.Sex;
import java.util.List;
import org.jspecify.annotations.NullMarked;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {
    List<User> findAllByName(String name);

    @Query("select u from User u where u.measurements.sex = :sex")
    List<User> findAllBySex(@Param("sex") Sex sex);

    @Query("select u from User u where u.measurements.age = :age")
    List<User> findAllByAge(@Param("age") Integer age);

    @Query("SELECT DISTINCT users FROM User users LEFT JOIN FETCH users.mealsPlan")
    List<User> findAllWithMeals();

    @Override
    @EntityGraph(attributePaths = {"mealsPlan", "mealsPlan.recipe"})
    @NullMarked
    List<User> findAll();
}
