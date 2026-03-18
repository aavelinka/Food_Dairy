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

import com.uni.project.cache.UserQueryKey;
import com.uni.project.cache.UserSearchCache;
import com.uni.project.exception.EmailAlreadyExistsException;
import com.uni.project.mapper.UserMapper;
import com.uni.project.model.dto.request.BodyParametersRequest;
import com.uni.project.model.dto.request.UserRequest;
import com.uni.project.model.dto.response.UserResponse;
import com.uni.project.model.entity.BodyParameters;
import com.uni.project.model.entity.GoalType;
import com.uni.project.model.entity.NutritionalValue;
import com.uni.project.model.entity.Sex;
import com.uni.project.model.entity.User;
import com.uni.project.repository.UserRepository;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {
    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @Mock
    private NutritionalGoalCalculator nutritionalGoalCalculator;

    @Mock
    private UserSearchCache userSearchCache;

    @InjectMocks
    private UserServiceImpl userService;

    @Test
    void userCreateShouldInitializeBodyParametersAndClearCache() {
        UserRequest request = buildRequest("user@example.com", LocalDate.of(2026, 3, 18), GoalType.MAINTENANCE);
        User user = buildUser(1, "user@example.com", GoalType.MAINTENANCE);
        UserResponse expectedResponse = new UserResponse();
        NutritionalValue goal = buildGoal(2200.0);

        when(userRepository.existsByEmailIgnoreCase("user@example.com")).thenReturn(false);
        when(userMapper.fromRequest(request)).thenReturn(user);
        when(nutritionalGoalCalculator.calculate(any(BodyParameters.class), eq(GoalType.MAINTENANCE))).thenReturn(goal);
        when(userRepository.save(same(user))).thenReturn(user);
        when(userMapper.toResponse(user)).thenReturn(expectedResponse);

        UserResponse actualResponse = userService.userCreate(request);

        assertSame(expectedResponse, actualResponse);
        assertEquals(1, user.getBodyParametersHistory().size());
        BodyParameters initialBodyParameters = user.getBodyParametersHistory().iterator().next();
        assertSame(user, initialBodyParameters.getOwner());
        assertSame(goal, initialBodyParameters.getGoalNutritional());
        assertEquals(Boolean.TRUE, initialBodyParameters.getAutoCalculated());
        verify(userSearchCache).clear();
    }

    @Test
    void userCreateShouldThrowWhenEmailAlreadyExists() {
        UserRequest request = buildRequest("taken@example.com", LocalDate.of(2026, 3, 18), GoalType.MAINTENANCE);
        when(userRepository.existsByEmailIgnoreCase("taken@example.com")).thenReturn(true);

        assertThrows(EmailAlreadyExistsException.class, () -> userService.userCreate(request));
    }

    @Test
    void getUserByIdShouldReturnMappedResponse() {
        User user = buildUser(2, "user@example.com", GoalType.MAINTENANCE);
        UserResponse expectedResponse = new UserResponse();

        when(userRepository.findById(2)).thenReturn(Optional.of(user));
        when(userMapper.toResponse(user)).thenReturn(expectedResponse);

        UserResponse actualResponse = userService.getUserById(2);

        assertSame(expectedResponse, actualResponse);
    }

    @Test
    void getAllUsersShouldReturnCachedPage() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<UserResponse> cachedPage = new PageImpl<>(List.of(new UserResponse()), pageable, 1);

        when(userSearchCache.get(any(UserQueryKey.class))).thenReturn(Optional.of(cachedPage));

        Page<UserResponse> actualPage = userService.getAllUsers(pageable);

        assertSame(cachedPage, actualPage);
        verify(userRepository, never()).findAll(pageable);
    }

    @Test
    void getAllUsersShouldLoadUsersFromRepositoryAndCacheResult() {
        Pageable pageable = PageRequest.of(0, 10);
        User firstUser = buildUser(1, "first@example.com", GoalType.MAINTENANCE);
        User secondUser = buildUser(2, "second@example.com", GoalType.WEIGHT_GAIN);
        Page<User> usersPage = new PageImpl<>(List.of(firstUser, secondUser), pageable, 2);
        UserResponse firstResponse = new UserResponse();
        UserResponse secondResponse = new UserResponse();

        when(userSearchCache.get(any(UserQueryKey.class))).thenReturn(Optional.empty());
        when(userRepository.findAll(pageable)).thenReturn(usersPage);
        when(userMapper.toResponse(firstUser)).thenReturn(firstResponse);
        when(userMapper.toResponse(secondUser)).thenReturn(secondResponse);

        Page<UserResponse> actualPage = userService.getAllUsers(pageable);

        assertEquals(List.of(firstResponse, secondResponse), actualPage.getContent());
        verify(userSearchCache).put(any(UserQueryKey.class), org.mockito.ArgumentMatchers.<Page<UserResponse>>any());
    }

    @Test
    void userUpdateShouldAllowSameEmailAndCopyGoalFromLatestMeasurement() {
        LocalDate newDate = LocalDate.of(2026, 3, 20);
        UserRequest request = buildRequest("same@example.com", newDate, GoalType.WEIGHT_GAIN);
        User user = buildUser(3, "same@example.com", GoalType.MAINTENANCE);
        BodyParameters latest = buildBodyParameters(11, LocalDate.of(2026, 3, 10), user);
        NutritionalValue latestGoal = buildGoal(2000.0);
        latest.setGoalNutritional(latestGoal);
        latest.setAutoCalculated(false);
        user.setBodyParametersHistory(new HashSet<>(List.of(latest)));
        UserResponse expectedResponse = new UserResponse();

        when(userRepository.findById(3)).thenReturn(Optional.of(user));
        when(userRepository.existsByEmailIgnoreCase("same@example.com")).thenReturn(true);
        when(userRepository.save(same(user))).thenReturn(user);
        when(userMapper.toResponse(user)).thenReturn(expectedResponse);

        UserResponse actualResponse = userService.userUpdate(3, request);

        assertSame(expectedResponse, actualResponse);
        assertEquals(request.getName(), user.getName());
        assertEquals(request.getPassword(), user.getPassword());
        assertEquals(request.getEmail(), user.getEmail());
        assertEquals(request.getGoalType(), user.getGoalType());
        assertEquals(2, user.getBodyParametersHistory().size());
        BodyParameters newBodyParameters = user.getBodyParametersHistory().stream()
                .filter(bodyParameters -> newDate.equals(bodyParameters.getRecordDate()))
                .findFirst()
                .orElseThrow();
        assertNotSame(latestGoal, newBodyParameters.getGoalNutritional());
        assertEquals(latestGoal.getCalories(), newBodyParameters.getGoalNutritional().getCalories());
        assertFalse(newBodyParameters.getAutoCalculated());
        verify(nutritionalGoalCalculator, never()).calculate(any(), any());
        verify(userSearchCache).clear();
    }

    @Test
    void userUpdateShouldCalculateGoalWhenLatestMeasurementHasNoGoal() {
        LocalDate newDate = LocalDate.of(2026, 3, 21);
        UserRequest request = buildRequest("new@example.com", newDate, GoalType.WEIGHT_LOSS);
        User user = buildUser(4, "old@example.com", GoalType.MAINTENANCE);
        BodyParameters latest = buildBodyParameters(12, LocalDate.of(2026, 3, 11), user);
        user.setBodyParametersHistory(new HashSet<>(List.of(latest)));
        UserResponse expectedResponse = new UserResponse();
        NutritionalValue recalculatedGoal = buildGoal(1800.0);

        when(userRepository.findById(4)).thenReturn(Optional.of(user));
        when(userRepository.existsByEmailIgnoreCase("new@example.com")).thenReturn(false);
        when(nutritionalGoalCalculator.calculate(any(BodyParameters.class), eq(GoalType.WEIGHT_LOSS)))
                .thenReturn(recalculatedGoal);
        when(userRepository.save(same(user))).thenReturn(user);
        when(userMapper.toResponse(user)).thenReturn(expectedResponse);

        UserResponse actualResponse = userService.userUpdate(4, request);

        assertSame(expectedResponse, actualResponse);
        BodyParameters newBodyParameters = user.getBodyParametersHistory().stream()
                .filter(bodyParameters -> newDate.equals(bodyParameters.getRecordDate()))
                .findFirst()
                .orElseThrow();
        assertSame(recalculatedGoal, newBodyParameters.getGoalNutritional());
        assertEquals(Boolean.TRUE, newBodyParameters.getAutoCalculated());
    }

    @Test
    void userUpdateShouldThrowWhenEmailBelongsToAnotherUser() {
        UserRequest request = buildRequest("duplicate@example.com", LocalDate.of(2026, 3, 20), GoalType.MAINTENANCE);
        User user = buildUser(5, "old@example.com", GoalType.MAINTENANCE);

        when(userRepository.findById(5)).thenReturn(Optional.of(user));
        when(userRepository.existsByEmailIgnoreCase("duplicate@example.com")).thenReturn(true);

        assertThrows(EmailAlreadyExistsException.class, () -> userService.userUpdate(5, request));
    }

    @Test
    void userDeleteShouldDeleteUserAndClearCache() {
        User user = buildUser(6, "delete@example.com", GoalType.MAINTENANCE);
        when(userRepository.findById(6)).thenReturn(Optional.of(user));

        userService.userDelete(6);

        verify(userRepository).delete(user);
        verify(userSearchCache).clear();
    }

    @Test
    void getAllUsersByNameShouldReturnMappedResponses() {
        List<User> users = List.of(buildUser(1, "name@example.com", GoalType.MAINTENANCE));
        List<UserResponse> expectedResponses = List.of(new UserResponse());

        when(userRepository.findAllByName("Anton")).thenReturn(users);
        when(userMapper.toResponses(users)).thenReturn(expectedResponses);

        List<UserResponse> actualResponses = userService.getAllUsersByName("Anton");

        assertEquals(expectedResponses, actualResponses);
    }

    @Test
    void getAllUsersBySexShouldReturnMappedResponses() {
        List<User> users = List.of(buildUser(1, "sex@example.com", GoalType.MAINTENANCE));
        List<UserResponse> expectedResponses = List.of(new UserResponse());

        when(userRepository.findAllBySex(Sex.FEMALE)).thenReturn(users);
        when(userMapper.toResponses(users)).thenReturn(expectedResponses);

        List<UserResponse> actualResponses = userService.getAllUsersBySex(Sex.FEMALE);

        assertEquals(expectedResponses, actualResponses);
    }

    @Test
    void getAllUsersByAgeShouldLoadAndCachePage() {
        Pageable pageable = PageRequest.of(0, 5);
        User user = buildUser(1, "age@example.com", GoalType.MAINTENANCE);
        UserResponse response = new UserResponse();
        Page<User> usersPage = new PageImpl<>(List.of(user), pageable, 1);

        when(userSearchCache.get(any(UserQueryKey.class))).thenReturn(Optional.empty());
        when(userRepository.findAllByAge(25, pageable)).thenReturn(usersPage);
        when(userMapper.toResponse(user)).thenReturn(response);

        Page<UserResponse> actualPage = userService.getAllUsersByAge(25, pageable);

        assertEquals(List.of(response), actualPage.getContent());
        verify(userSearchCache).put(any(UserQueryKey.class), org.mockito.ArgumentMatchers.<Page<UserResponse>>any());
    }

    @Test
    void getAllUsersByAgeNativeShouldReturnCachedPage() {
        Pageable pageable = PageRequest.of(1, 5);
        Page<UserResponse> cachedPage = new PageImpl<>(List.of(new UserResponse()), pageable, 1);

        when(userSearchCache.get(any(UserQueryKey.class))).thenReturn(Optional.of(cachedPage));

        Page<UserResponse> actualPage = userService.getAllUsersByAgeNative(30, pageable);

        assertSame(cachedPage, actualPage);
        verify(userRepository, never()).findAllByAgeNative(30, pageable);
    }

    @Test
    void findAllWithMealsAndBodyParametersShouldReturnMappedResponses() {
        List<User> users = List.of(buildUser(1, "joined@example.com", GoalType.MAINTENANCE));
        List<UserResponse> expectedResponses = List.of(new UserResponse());

        when(userRepository.findAllWithMealsAndBodyParameters()).thenReturn(users);
        when(userMapper.toResponses(users)).thenReturn(expectedResponses);

        List<UserResponse> actualResponses = userService.findAllWithMealsAndBodyParameters();

        assertEquals(expectedResponses, actualResponses);
    }

    private UserRequest buildRequest(String email, LocalDate recordDate, GoalType goalType) {
        return new UserRequest(
                "Anton",
                "secret123",
                email,
                new BodyParametersRequest(recordDate, Sex.FEMALE, 60.0, 170.0, 25, 90.0, 70.0, 95.0, 1),
                goalType
        );
    }

    private User buildUser(Integer id, String email, GoalType goalType) {
        User user = new User();
        user.setId(id);
        user.setEmail(email);
        user.setGoalType(goalType);
        user.setName("Anton");
        user.setPassword("secret123");
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
