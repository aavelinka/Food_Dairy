package com.uni.project.service;

import com.uni.project.model.dto.request.UserRequest;
import com.uni.project.model.dto.request.UserCompositeRequest;
import com.uni.project.model.dto.response.UserResponse;
import com.uni.project.model.entity.Sex;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface UserService {
    UserResponse userCreate(UserRequest userRequest);

    UserResponse getUserById(Integer id);

    Page<UserResponse> getAllUsers(Pageable pageable);

    UserResponse userUpdate(Integer id, UserRequest userRequest);

    void userDelete(Integer id);

    List<UserResponse> getAllUsersByName(String nameSearch);

    List<UserResponse> getAllUsersBySex(Sex sexSearch);

    Page<UserResponse> getAllUsersByAge(Integer ageSearch, Pageable pageable);

    Page<UserResponse> getAllUsersByAgeNative(Integer ageSearch, Pageable pageable);

    List<UserResponse> findAllWithMealsAndBodyParameters();

    UserResponse createUserWithoutGoalAndNoteNoTx(UserCompositeRequest userRequest);

    UserResponse createUserWithGoalAndNoteTx(UserCompositeRequest userRequest);
}
