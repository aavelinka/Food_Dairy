package com.uni.project.service.impl;

import com.uni.project.exception.WaterIntakeException;
import com.uni.project.mapper.WaterIntakeMapper;
import com.uni.project.model.dto.request.WaterIntakeRequest;
import com.uni.project.model.dto.response.WaterIntakeResponse;
import com.uni.project.model.entity.WaterIntake;
import com.uni.project.repository.WaterIntakeRepository;
import com.uni.project.service.WaterIntakeService;
import java.time.LocalDate;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@AllArgsConstructor
@Transactional(readOnly = true)
public class WaterIntakeServiceImpl implements WaterIntakeService {
    private static final String WATER_INTAKE_FAIL_MESSAGE = "Water intake not found by Id";
    private final WaterIntakeRepository waterIntakeRepository;
    private final WaterIntakeMapper waterIntakeMapper;

    @Override
    @Transactional
    public WaterIntakeResponse waterIntakeCreate(WaterIntakeRequest waterIntakeRequest) {
        WaterIntake waterIntake = waterIntakeRepository.save(waterIntakeMapper.fromRequest(waterIntakeRequest));
        return waterIntakeMapper.toResponse(waterIntake);
    }

    @Override
    public WaterIntakeResponse getWaterIntakeById(Integer id) {
        WaterIntake waterIntake = waterIntakeRepository.findById(id)
                .orElseThrow(() -> new WaterIntakeException(WATER_INTAKE_FAIL_MESSAGE));
        return waterIntakeMapper.toResponse(waterIntake);
    }

    @Override
    public List<WaterIntakeResponse> getAllWaterIntakes() {
        return waterIntakeMapper.toResponses(waterIntakeRepository.findAll());
    }

    @Override
    @Transactional
    public WaterIntakeResponse waterIntakeUpdate(Integer id, WaterIntakeRequest waterIntakeRequest) {
        WaterIntake waterIntake = waterIntakeRepository.findById(id)
                .orElseThrow(() -> new WaterIntakeException(WATER_INTAKE_FAIL_MESSAGE));

        waterIntake.setUserId(waterIntakeRequest.getUserId());
        waterIntake.setDate(waterIntakeRequest.getDate());
        waterIntake.setAmountMl(waterIntakeRequest.getAmountMl());
        waterIntake.setDrinkType(waterIntakeRequest.getDrinkType());
        waterIntake.setComment(waterIntakeRequest.getComment());
        waterIntakeRepository.save(waterIntake);

        return waterIntakeMapper.toResponse(waterIntake);
    }

    @Override
    @Transactional
    public void waterIntakeDelete(Integer id) {
        WaterIntake waterIntake = waterIntakeRepository.findById(id)
                .orElseThrow(() -> new WaterIntakeException(WATER_INTAKE_FAIL_MESSAGE));
        waterIntakeRepository.delete(waterIntake);
    }

    @Override
    public List<WaterIntakeResponse> getAllWaterIntakesByUserId(Integer userId) {
        return waterIntakeMapper.toResponses(waterIntakeRepository.findAllByUserIdOrderByDateDesc(userId));
    }

    @Override
    public List<WaterIntakeResponse> getAllWaterIntakesByUserIdAndDate(Integer userId, LocalDate date) {
        return waterIntakeMapper.toResponses(waterIntakeRepository.findAllByUserIdAndDate(userId, date));
    }

    @Override
    public Integer getDailyTotalByUserIdAndDate(Integer userId, LocalDate date) {
        return waterIntakeRepository.getDailyTotalByUserIdAndDate(userId, date);
    }
}
