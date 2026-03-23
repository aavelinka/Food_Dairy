package com.uni.project.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.uni.project.mapper.WaterIntakeMapper;
import com.uni.project.model.dto.request.WaterIntakeRequest;
import com.uni.project.model.dto.response.WaterIntakeResponse;
import com.uni.project.model.entity.WaterIntake;
import com.uni.project.repository.WaterIntakeRepository;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class WaterIntakeServiceImplTest {
    @Mock
    private WaterIntakeRepository waterIntakeRepository;

    @Mock
    private WaterIntakeMapper waterIntakeMapper;

    @InjectMocks
    private WaterIntakeServiceImpl waterIntakeService;

    @Test
    void waterIntakeCreateShouldSaveMappedEntity() {
        WaterIntakeRequest request = buildRequest();
        WaterIntake mappedWaterIntake = new WaterIntake();
        WaterIntake savedWaterIntake = new WaterIntake();
        WaterIntakeResponse expectedResponse = new WaterIntakeResponse();

        when(waterIntakeMapper.fromRequest(request)).thenReturn(mappedWaterIntake);
        when(waterIntakeRepository.save(mappedWaterIntake)).thenReturn(savedWaterIntake);
        when(waterIntakeMapper.toResponse(savedWaterIntake)).thenReturn(expectedResponse);

        WaterIntakeResponse actualResponse = waterIntakeService.waterIntakeCreate(request);

        assertSame(expectedResponse, actualResponse);
    }

    @Test
    void getWaterIntakeByIdShouldReturnMappedResponse() {
        WaterIntake waterIntake = new WaterIntake();
        WaterIntakeResponse expectedResponse = new WaterIntakeResponse();

        when(waterIntakeRepository.findById(1)).thenReturn(Optional.of(waterIntake));
        when(waterIntakeMapper.toResponse(waterIntake)).thenReturn(expectedResponse);

        WaterIntakeResponse actualResponse = waterIntakeService.getWaterIntakeById(1);

        assertSame(expectedResponse, actualResponse);
    }

    @Test
    void getAllWaterIntakesShouldReturnMappedResponses() {
        List<WaterIntake> waterIntakes = List.of(new WaterIntake());
        List<WaterIntakeResponse> expectedResponses = List.of(new WaterIntakeResponse());

        when(waterIntakeRepository.findAll()).thenReturn(waterIntakes);
        when(waterIntakeMapper.toResponses(waterIntakes)).thenReturn(expectedResponses);

        List<WaterIntakeResponse> actualResponses = waterIntakeService.getAllWaterIntakes();

        assertEquals(expectedResponses, actualResponses);
    }

    @Test
    void waterIntakeUpdateShouldUpdateFieldsAndSaveEntity() {
        WaterIntakeRequest request = buildRequest();
        WaterIntake existingWaterIntake = new WaterIntake();
        WaterIntakeResponse expectedResponse = new WaterIntakeResponse();

        when(waterIntakeRepository.findById(2)).thenReturn(Optional.of(existingWaterIntake));
        when(waterIntakeRepository.save(existingWaterIntake)).thenReturn(existingWaterIntake);
        when(waterIntakeMapper.toResponse(existingWaterIntake)).thenReturn(expectedResponse);

        WaterIntakeResponse actualResponse = waterIntakeService.waterIntakeUpdate(2, request);

        assertSame(expectedResponse, actualResponse);
        assertEquals(request.getUserId(), existingWaterIntake.getUserId());
        assertEquals(request.getDate(), existingWaterIntake.getDate());
        assertEquals(request.getAmountMl(), existingWaterIntake.getAmountMl());
        assertEquals(request.getDrinkType(), existingWaterIntake.getDrinkType());
        assertEquals(request.getComment(), existingWaterIntake.getComment());
    }

    @Test
    void waterIntakeDeleteShouldDeleteFoundEntity() {
        WaterIntake waterIntake = new WaterIntake();
        when(waterIntakeRepository.findById(3)).thenReturn(Optional.of(waterIntake));

        waterIntakeService.waterIntakeDelete(3);

        verify(waterIntakeRepository).delete(waterIntake);
    }

    @Test
    void getAllWaterIntakesByUserIdShouldReturnMappedResponses() {
        List<WaterIntake> waterIntakes = List.of(new WaterIntake());
        List<WaterIntakeResponse> expectedResponses = List.of(new WaterIntakeResponse());

        when(waterIntakeRepository.findAllByUserIdOrderByDateDesc(4)).thenReturn(waterIntakes);
        when(waterIntakeMapper.toResponses(waterIntakes)).thenReturn(expectedResponses);

        List<WaterIntakeResponse> actualResponses = waterIntakeService.getAllWaterIntakesByUserId(4);

        assertEquals(expectedResponses, actualResponses);
    }

    @Test
    void getAllWaterIntakesByUserIdAndDateShouldReturnMappedResponses() {
        LocalDate date = LocalDate.of(2026, 3, 18);
        List<WaterIntake> waterIntakes = List.of(new WaterIntake());
        List<WaterIntakeResponse> expectedResponses = List.of(new WaterIntakeResponse());

        when(waterIntakeRepository.findAllByUserIdAndDate(5, date)).thenReturn(waterIntakes);
        when(waterIntakeMapper.toResponses(waterIntakes)).thenReturn(expectedResponses);

        List<WaterIntakeResponse> actualResponses = waterIntakeService.getAllWaterIntakesByUserIdAndDate(5, date);

        assertEquals(expectedResponses, actualResponses);
    }

    @Test
    void getDailyTotalByUserIdAndDateShouldReturnRepositoryValue() {
        LocalDate date = LocalDate.of(2026, 3, 18);
        when(waterIntakeRepository.getDailyTotalByUserIdAndDate(7, date)).thenReturn(1800);

        Integer actualTotal = waterIntakeService.getDailyTotalByUserIdAndDate(7, date);

        assertEquals(1800, actualTotal);
    }

    private WaterIntakeRequest buildRequest() {
        return new WaterIntakeRequest(
                1,
                LocalDate.of(2026, 3, 18),
                250,
                "Water",
                "After workout"
        );
    }
}
