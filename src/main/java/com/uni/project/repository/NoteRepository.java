package com.uni.project.repository;

import com.uni.project.model.entity.Note;
import java.time.LocalDate;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface NoteRepository extends JpaRepository<Note, Integer> {
    List<Note> findAllByDate(LocalDate date);

    @Query("select n from Note n where n.user.id = :userId ")
    List<Note> findAllByUserId(@Param("userId") Integer userId);

    @Query(" select n from Note n where n.meal.id = :mealId ")
    List<Note> findAllByMealId(@Param("mealId") Integer mealId);
}
