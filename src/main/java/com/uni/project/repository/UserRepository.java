package com.uni.project.repository;

import com.uni.project.model.entity.User;
import com.uni.project.model.entity.Sex;
import java.util.List;
import org.jspecify.annotations.NullMarked;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {
    List<User> findAllByName(String name);

    @Query("select distinct u from User u join u.bodyParametersHistory bp where bp.sex = :sex")
    List<User> findAllBySex(@Param("sex") Sex sex);

    @Query(
            value = "select distinct u from User u join u.bodyParametersHistory bp where bp.age = :age",
            countQuery = "select count(distinct u.id) from User u join u.bodyParametersHistory bp where bp.age = :age"
    )
    Page<User> findAllByAge(@Param("age") Integer age, Pageable pageable);

    @Query(
            value = "select distinct u.* "
                    + "from users u "
                    + "join body_parameters bp on bp.user_id = u.id "
                    + "where bp.age = :age",
            countQuery = "select count(distinct u.id) "
                    + "from users u "
                    + "join body_parameters bp on bp.user_id = u.id "
                    + "where bp.age = :age",
            nativeQuery = true
    )
    Page<User> findAllByAgeNative(@Param("age") Integer age, Pageable pageable);

    @Query("select distinct u from User u left join fetch u.mealsPlan mp " +
            "left join fetch mp.recipe left join fetch u.bodyParametersHistory bph")
    List<User> findAllWithMealsAndBodyParameters();

    @Override
    @EntityGraph(attributePaths = {"mealsPlan", "mealsPlan.recipe", "bodyParametersHistory"})
    @NullMarked
    List<User> findAll();

    @Override
    @EntityGraph(attributePaths = {"mealsPlan", "mealsPlan.recipe", "bodyParametersHistory"})
    @NullMarked
    Page<User> findAll(Pageable pageable);
}
