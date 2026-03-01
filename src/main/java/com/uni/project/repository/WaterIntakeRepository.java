package com.uni.project.repository;

import com.uni.project.model.entity.WaterIntake;
import java.time.LocalDate;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface WaterIntakeRepository extends JpaRepository<WaterIntake, Integer> {
    List<WaterIntake> findAllByUserIdOrderByDateDesc(Integer userId);

    List<WaterIntake> findAllByUserIdAndDate(Integer userId, LocalDate date);

    @Query("select coalesce(sum(w.amountMl), 0) "
            + "from WaterIntake w where w.userId = :userId and w.date = :date")
    Integer getDailyTotalByUserIdAndDate(@Param("userId") Integer userId,
                                         @Param("date") LocalDate date);
}
