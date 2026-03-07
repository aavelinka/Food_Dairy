package com.uni.project.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import com.uni.project.mapper.NutritionalValueMapper;
import com.uni.project.mapper.UserMapper;
import com.uni.project.model.dto.response.UserResponse;
import com.uni.project.model.entity.User;
import com.uni.project.repository.MealRepository;
import com.uni.project.repository.UserRepository;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {
    @Mock
    private UserRepository userRepository;

    @Mock
    private MealRepository mealRepository;

    @Mock
    private UserMapper userMapper;

    @Mock
    private NutritionalValueMapper nutritionalValueMapper;

    @InjectMocks
    private UserServiceImpl userService;

    @Test
    void getAllUsersByAgeNativeReturnsMappedResponses() {
        User user = new User();
        user.setId(1);

        UserResponse response = new UserResponse();
        response.setId(1);

        when(userRepository.findAllByAgeNative(25)).thenReturn(List.of(user));
        when(userMapper.toResponses(List.of(user))).thenReturn(List.of(response));

        List<UserResponse> result = userService.getAllUsersByAgeNative(25);

        assertEquals(1, result.size());
        assertEquals(1, result.getFirst().getId());
    }
}
