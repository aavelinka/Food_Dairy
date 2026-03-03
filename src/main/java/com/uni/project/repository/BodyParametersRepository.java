package com.uni.project.repository;

import com.uni.project.model.entity.BodyParameters;
import java.time.LocalDate;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BodyParametersRepository extends JpaRepository<BodyParameters, Integer> {
    List<BodyParameters> findAllByOwnerIdOrderByRecordDateDesc(Integer userId);

    List<BodyParameters> findAllByOwnerIdAndRecordDate(Integer userId, LocalDate recordDate);
}
