package com.uni.project.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.same;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.uni.project.cache.UserSearchCache;
import com.uni.project.exception.BodyParametersBadRequestException;
import com.uni.project.mapper.BodyParametersMapper;
import com.uni.project.mapper.NutritionalValueMapper;
import com.uni.project.model.dto.request.BodyParametersRequest;
import com.uni.project.model.dto.request.NutritionalValueRequest;
import com.uni.project.model.dto.response.BodyParametersResponse;
import com.uni.project.model.dto.response.NutritionalValueResponse;
import com.uni.project.model.entity.BodyParameters;
import com.uni.project.model.entity.GoalType;
import com.uni.project.model.entity.NutritionalValue;
import com.uni.project.model.entity.Sex;
import com.uni.project.model.entity.User;
import com.uni.project.repository.BodyParametersRepository;
import com.uni.project.repository.UserRepository;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class BodyParametersServiceImplTest {
    @Mock
    private BodyParametersRepository bodyParametersRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private BodyParametersMapper bodyParametersMapper;

    @Mock
    private NutritionalValueMapper nutritionalValueMapper;

    @Mock
    private NutritionalGoalCalculator nutritionalGoalCalculator;

    @Mock
    private UserSearchCache userSearchCache;

    @InjectMocks
    private BodyParametersServiceImpl bodyParametersService;

    @Test
    void bodyParametersCreateShouldCalculateGoalForFirstMeasurement() {
        BodyParametersRequest request = buildRequest(1, LocalDate.of(2026, 3, 18));
        User owner = buildUser(1, GoalType.MAINTENANCE);
        owner.setBodyParametersHistory(Set.of());
        BodyParameters mappedBodyParameters = buildBodyParameters(null, request.getRecordDate(), null);
        BodyParameters savedBodyParameters = buildBodyParameters(10, request.getRecordDate(), owner);
        BodyParametersResponse expectedResponse = new BodyParametersResponse();
        NutritionalValue goal = buildGoal(2100.0);

        when(userRepository.findById(1)).thenReturn(Optional.of(owner));
        when(bodyParametersMapper.fromRequest(request)).thenReturn(mappedBodyParameters);
        when(nutritionalGoalCalculator.calculate(same(mappedBodyParameters), eq(GoalType.MAINTENANCE))).thenReturn(goal);
        when(bodyParametersRepository.save(same(mappedBodyParameters))).thenReturn(savedBodyParameters);
        when(bodyParametersMapper.toResponse(savedBodyParameters)).thenReturn(expectedResponse);

        BodyParametersResponse actualResponse = bodyParametersService.bodyParametersCreate(request);

        assertSame(expectedResponse, actualResponse);
        assertSame(owner, mappedBodyParameters.getOwner());
        assertSame(goal, mappedBodyParameters.getGoalNutritional());
        assertEquals(Boolean.TRUE, mappedBodyParameters.getAutoCalculated());
        verify(userSearchCache).clear();
    }

    @Test
    void bodyParametersCreateShouldReuseLatestGoalWhenItExists() {
        BodyParametersRequest request = buildRequest(2, LocalDate.of(2026, 3, 18));
        User owner = buildUser(2, GoalType.WEIGHT_GAIN);
        BodyParameters latest = buildBodyParameters(7, LocalDate.of(2026, 3, 10), owner);
        NutritionalValue latestGoal = buildGoal(2300.0);
        latest.setGoalNutritional(latestGoal);
        latest.setAutoCalculated(false);
        owner.setBodyParametersHistory(Set.of(latest));
        BodyParameters mappedBodyParameters = buildBodyParameters(null, request.getRecordDate(), null);
        BodyParameters savedBodyParameters = buildBodyParameters(11, request.getRecordDate(), owner);
        BodyParametersResponse expectedResponse = new BodyParametersResponse();

        when(userRepository.findById(2)).thenReturn(Optional.of(owner));
        when(bodyParametersMapper.fromRequest(request)).thenReturn(mappedBodyParameters);
        when(bodyParametersRepository.save(same(mappedBodyParameters))).thenReturn(savedBodyParameters);
        when(bodyParametersMapper.toResponse(savedBodyParameters)).thenReturn(expectedResponse);

        BodyParametersResponse actualResponse = bodyParametersService.bodyParametersCreate(request);

        assertSame(expectedResponse, actualResponse);
        assertNotSame(latestGoal, mappedBodyParameters.getGoalNutritional());
        assertEquals(latestGoal.getCalories(), mappedBodyParameters.getGoalNutritional().getCalories());
        assertFalse(mappedBodyParameters.getAutoCalculated());
        verify(nutritionalGoalCalculator, never()).calculate(any(), any());
    }

    @Test
    void bodyParametersCreateShouldThrowWhenUserIdIsMissing() {
        BodyParametersRequest request = buildRequest(null, LocalDate.of(2026, 3, 18));

        assertThrows(BodyParametersBadRequestException.class, () -> bodyParametersService.bodyParametersCreate(request));
    }

    @Test
    void getBodyParametersByIdShouldReturnMappedResponse() {
        BodyParameters bodyParameters = buildBodyParameters(3, LocalDate.of(2026, 3, 18), buildUser(1, GoalType.MAINTENANCE));
        BodyParametersResponse expectedResponse = new BodyParametersResponse();

        when(bodyParametersRepository.findById(3)).thenReturn(Optional.of(bodyParameters));
        when(bodyParametersMapper.toResponse(bodyParameters)).thenReturn(expectedResponse);

        BodyParametersResponse actualResponse = bodyParametersService.getBodyParametersById(3);

        assertSame(expectedResponse, actualResponse);
    }

    @Test
    void getAllBodyParametersShouldReturnMappedResponses() {
        List<BodyParameters> bodyParametersList = List.of(buildBodyParameters(1, LocalDate.of(2026, 3, 18), null));
        List<BodyParametersResponse> expectedResponses = List.of(new BodyParametersResponse());

        when(bodyParametersRepository.findAll()).thenReturn(bodyParametersList);
        when(bodyParametersMapper.toResponses(bodyParametersList)).thenReturn(expectedResponses);

        List<BodyParametersResponse> actualResponses = bodyParametersService.getAllBodyParameters();

        assertEquals(expectedResponses, actualResponses);
    }

    @Test
    void bodyParametersUpdateShouldRecalculateGoalWhenCurrentGoalIsMissing() {
        BodyParametersRequest request = buildRequest(4, LocalDate.of(2026, 3, 19));
        User owner = buildUser(4, GoalType.WEIGHT_LOSS);
        owner.setBodyParametersHistory(Set.of());
        BodyParameters existing = buildBodyParameters(12, LocalDate.of(2026, 3, 1), null);
        BodyParametersResponse expectedResponse = new BodyParametersResponse();
        NutritionalValue recalculatedGoal = buildGoal(1800.0);

        when(bodyParametersRepository.findById(12)).thenReturn(Optional.of(existing));
        when(userRepository.findById(4)).thenReturn(Optional.of(owner));
        when(nutritionalGoalCalculator.calculate(same(existing), eq(GoalType.WEIGHT_LOSS))).thenReturn(recalculatedGoal);
        when(bodyParametersRepository.save(same(existing))).thenReturn(existing);
        when(bodyParametersMapper.toResponse(existing)).thenReturn(expectedResponse);

        BodyParametersResponse actualResponse = bodyParametersService.bodyParametersUpdate(12, request);

        assertSame(expectedResponse, actualResponse);
        assertSame(owner, existing.getOwner());
        assertEquals(request.getRecordDate(), existing.getRecordDate());
        assertEquals(request.getSex(), existing.getSex());
        assertSame(recalculatedGoal, existing.getGoalNutritional());
        assertEquals(Boolean.TRUE, existing.getAutoCalculated());
        verify(userSearchCache).clear();
    }

    @Test
    void bodyParametersDeleteShouldDeleteEntityAndClearCache() {
        BodyParameters bodyParameters = buildBodyParameters(13, LocalDate.of(2026, 3, 18), null);
        when(bodyParametersRepository.findById(13)).thenReturn(Optional.of(bodyParameters));

        bodyParametersService.bodyParametersDelete(13);

        verify(bodyParametersRepository).delete(bodyParameters);
        verify(userSearchCache).clear();
    }

    @Test
    void getAllBodyParametersByUserIdShouldReturnMappedResponses() {
        List<BodyParameters> bodyParametersList = List.of(buildBodyParameters(1, LocalDate.of(2026, 3, 18), null));
        List<BodyParametersResponse> expectedResponses = List.of(new BodyParametersResponse());

        when(bodyParametersRepository.findAllByOwnerIdOrderByRecordDateDesc(5)).thenReturn(bodyParametersList);
        when(bodyParametersMapper.toResponses(bodyParametersList)).thenReturn(expectedResponses);

        List<BodyParametersResponse> actualResponses = bodyParametersService.getAllBodyParametersByUserId(5);

        assertEquals(expectedResponses, actualResponses);
    }

    @Test
    void getAllBodyParametersByUserIdAndDateShouldReturnMappedResponses() {
        LocalDate date = LocalDate.of(2026, 3, 18);
        List<BodyParameters> bodyParametersList = List.of(buildBodyParameters(1, date, null));
        List<BodyParametersResponse> expectedResponses = List.of(new BodyParametersResponse());

        when(bodyParametersRepository.findAllByOwnerIdAndRecordDate(6, date)).thenReturn(bodyParametersList);
        when(bodyParametersMapper.toResponses(bodyParametersList)).thenReturn(expectedResponses);

        List<BodyParametersResponse> actualResponses = bodyParametersService.getAllBodyParametersByUserIdAndDate(6, date);

        assertEquals(expectedResponses, actualResponses);
    }

    @Test
    void calculateNutritionalValueForUserShouldSaveCalculatedGoal() {
        User user = buildUser(7, GoalType.MAINTENANCE);
        BodyParameters older = buildBodyParameters(1, LocalDate.of(2026, 3, 10), user);
        BodyParameters latest = buildBodyParameters(2, LocalDate.of(2026, 3, 18), user);
        NutritionalValue goal = buildGoal(2050.0);
        NutritionalValueResponse expectedResponse = new NutritionalValueResponse();
        user.setBodyParametersHistory(Set.of(older, latest));

        when(userRepository.findById(7)).thenReturn(Optional.of(user));
        when(nutritionalGoalCalculator.calculate(same(latest), eq(GoalType.MAINTENANCE))).thenReturn(goal);
        when(bodyParametersRepository.save(same(latest))).thenReturn(latest);
        when(nutritionalValueMapper.toResponse(goal)).thenReturn(expectedResponse);

        NutritionalValueResponse actualResponse = bodyParametersService.calculateNutritionalValueForUser(7);

        assertSame(expectedResponse, actualResponse);
        assertSame(goal, latest.getGoalNutritional());
        assertEquals(Boolean.TRUE, latest.getAutoCalculated());
        verify(userSearchCache).clear();
    }

    @Test
    void calculateNutritionalValueForUserShouldThrowForIncompleteMeasurements() {
        User user = buildUser(8, GoalType.MAINTENANCE);
        BodyParameters incomplete = buildBodyParameters(3, LocalDate.of(2026, 3, 18), user);
        incomplete.setWeight(null);
        user.setBodyParametersHistory(Set.of(incomplete));

        when(userRepository.findById(8)).thenReturn(Optional.of(user));

        assertThrows(
                BodyParametersBadRequestException.class,
                () -> bodyParametersService.calculateNutritionalValueForUser(8)
        );
    }

    @Test
    void setManualNutritionalValueShouldPersistManualGoal() {
        BodyParameters bodyParameters = buildBodyParameters(14, LocalDate.of(2026, 3, 18), buildUser(9, GoalType.MAINTENANCE));
        NutritionalValueRequest request = new NutritionalValueRequest(1900.0, 120.0, 55.0, 200.0);
        NutritionalValue manualGoal = buildGoal(1900.0);
        NutritionalValueResponse expectedResponse = new NutritionalValueResponse();

        when(bodyParametersRepository.findById(14)).thenReturn(Optional.of(bodyParameters));
        when(nutritionalValueMapper.fromRequest(request)).thenReturn(manualGoal);
        when(bodyParametersRepository.save(same(bodyParameters))).thenReturn(bodyParameters);
        when(nutritionalValueMapper.toResponse(manualGoal)).thenReturn(expectedResponse);

        NutritionalValueResponse actualResponse = bodyParametersService.setManualNutritionalValue(14, request);

        assertSame(expectedResponse, actualResponse);
        assertSame(manualGoal, bodyParameters.getGoalNutritional());
        assertFalse(bodyParameters.getAutoCalculated());
        verify(userSearchCache).clear();
    }

    private BodyParametersRequest buildRequest(Integer userId, LocalDate recordDate) {
        return new BodyParametersRequest(
                recordDate,
                Sex.FEMALE,
                60.0,
                170.0,
                25,
                90.0,
                70.0,
                95.0,
                userId
        );
    }

    private User buildUser(Integer id, GoalType goalType) {
        User user = new User();
        user.setId(id);
        user.setGoalType(goalType);
        return user;
    }

    private BodyParameters buildBodyParameters(Integer id, LocalDate recordDate, User owner) {
        BodyParameters bodyParameters = new BodyParameters();
        bodyParameters.setId(id);
        bodyParameters.setRecordDate(recordDate);
        bodyParameters.setSex(Sex.FEMALE);
        bodyParameters.setWeight(60.0);
        bodyParameters.setHeight(170.0);
        bodyParameters.setAge(25);
        bodyParameters.setChest(90.0);
        bodyParameters.setWaist(70.0);
        bodyParameters.setHips(95.0);
        bodyParameters.setOwner(owner);
        return bodyParameters;
    }

    private NutritionalValue buildGoal(Double calories) {
        return new NutritionalValue(calories, calories * 0.25, calories * 0.2, calories * 0.55);
    }
}
