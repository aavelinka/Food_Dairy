package com.uni.project.service;

import com.uni.project.model.dto.request.WaterIntakeRequest;
import com.uni.project.model.dto.response.WaterIntakeResponse;
import java.time.LocalDate;
import java.util.List;

public interface WaterIntakeService {
    WaterIntakeResponse waterIntakeCreate(WaterIntakeRequest waterIntakeRequest);

    WaterIntakeResponse getWaterIntakeById(Integer id);

    List<WaterIntakeResponse> getAllWaterIntakes();

    WaterIntakeResponse waterIntakeUpdate(Integer id, WaterIntakeRequest waterIntakeRequest);

    void waterIntakeDelete(Integer id);

    List<WaterIntakeResponse> getAllWaterIntakesByUserId(Integer userId);

    List<WaterIntakeResponse> getAllWaterIntakesByUserIdAndDate(Integer userId, LocalDate date);

    Integer getDailyTotalByUserIdAndDate(Integer userId, LocalDate date);
}
