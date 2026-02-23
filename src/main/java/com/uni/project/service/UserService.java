package com.uni.project.service;

import com.uni.project.model.dto.request.UserRequest;
import com.uni.project.model.dto.request.UserCompositeRequest;
import com.uni.project.model.dto.request.UserMeasurementsRequest;
import com.uni.project.model.dto.response.NutritionalValueResponse;
import com.uni.project.model.dto.response.UserResponse;
import com.uni.project.model.entity.Sex;

import java.util.List;

public interface UserService {
    UserResponse userCreate(UserRequest userRequest);

    UserResponse getUserById(Integer id);

    List<UserResponse> getAllUsers();

    UserResponse userUpdate(Integer id, UserRequest userRequest);

    void userDelete(Integer id);

    UserResponse measurementsUpdate(Integer id, UserMeasurementsRequest userRequest);

    List<UserResponse> getAllUsersByName(String nameSearch);

    List<UserResponse> getAllUsersBySex(Sex sexSearch);

    List<UserResponse> getAllUsersByAge(Integer ageSearch);

    NutritionalValueResponse calculateNutritionalValueForUser(Integer id);

    List<UserResponse> findAllWithMeals();

    List<UserResponse> findAllWithNotes();

    UserResponse createUserWithGoalAndNoteNoTx(UserCompositeRequest userRequest);

    UserResponse createUserWithGoalAndNoteTx(UserCompositeRequest userRequest);
}
