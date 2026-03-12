package com.uni.project.controller.api;

import com.uni.project.model.dto.request.UserCompositeRequest;
import com.uni.project.model.dto.request.UserRequest;
import com.uni.project.model.dto.response.UserResponse;
import com.uni.project.model.entity.Sex;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;

@Tag(name = "Users", description = "Operations with users and their composite creation scenarios")
public interface UserControllerApi {
    @Operation(summary = "Get user by id")
    @ApiResponse(responseCode = "200", description = "User found")
    @BadRequestApiResponse
    @NotFoundApiResponse
    @InternalServerErrorApiResponse
    ResponseEntity<UserResponse> getUserById(@Parameter(description = "User id") @Positive Integer id);

    @Operation(summary = "Get all users")
    @ApiResponse(responseCode = "200", description = "Users returned")
    @InternalServerErrorApiResponse
    ResponseEntity<Page<UserResponse>> getAllUsers(Pageable pageable);

    @Operation(summary = "Create user")
    @ApiResponse(responseCode = "201", description = "User created")
    @BadRequestApiResponse
    @ConflictApiResponse
    @InternalServerErrorApiResponse
    ResponseEntity<UserResponse> userCreate(@Valid UserRequest userRequest);

    @Operation(summary = "Update user")
    @ApiResponse(responseCode = "200", description = "User updated")
    @BadRequestApiResponse
    @NotFoundApiResponse
    @ConflictApiResponse
    @InternalServerErrorApiResponse
    ResponseEntity<UserResponse> userUpdate(
            @Parameter(description = "User id") @Positive Integer id,
            @Valid UserRequest userRequest
    );

    @Operation(summary = "Delete user")
    @ApiResponse(responseCode = "204", description = "User deleted")
    @BadRequestApiResponse
    @NotFoundApiResponse
    @InternalServerErrorApiResponse
    ResponseEntity<Void> userDelete(@Parameter(description = "User id") @Positive Integer id);

    @Operation(summary = "Find users by exact name")
    @ApiResponse(responseCode = "200", description = "Users returned")
    @BadRequestApiResponse
    @InternalServerErrorApiResponse
    ResponseEntity<List<UserResponse>> getAllUsersByName(
            @Parameter(description = "Exact user name") @NotBlank String nameSearch
    );

    @Operation(summary = "Find users by sex")
    @ApiResponse(responseCode = "200", description = "Users returned")
    @BadRequestApiResponse
    @InternalServerErrorApiResponse
    ResponseEntity<List<UserResponse>> getAllUsersBySex(
            @Parameter(description = "User sex") Sex sexSearch
    );

    @Operation(summary = "Find users by age using JPQL")
    @ApiResponse(responseCode = "200", description = "Users returned")
    @BadRequestApiResponse
    @InternalServerErrorApiResponse
    ResponseEntity<Page<UserResponse>> getAllUsersByAge(
            @Parameter(description = "Age value") @Positive Integer ageSearch,
            Pageable pageable
    );

    @Operation(summary = "Find users by age using native SQL")
    @ApiResponse(responseCode = "200", description = "Users returned")
    @BadRequestApiResponse
    @InternalServerErrorApiResponse
    ResponseEntity<Page<UserResponse>> getAllUsersByAgeNative(
            @Parameter(description = "Age value") @Positive Integer ageSearch,
            Pageable pageable
    );

    @Operation(summary = "Get users with meals and body parameters")
    @ApiResponse(responseCode = "200", description = "Users returned")
    @InternalServerErrorApiResponse
    ResponseEntity<List<UserResponse>> findAllWithMealsAndBodyParameters();

    @Operation(summary = "Create user, meal and note without transaction")
    @ApiResponse(responseCode = "201", description = "Composite data created")
    @BadRequestApiResponse
    @ConflictApiResponse
    @InternalServerErrorApiResponse
    ResponseEntity<UserResponse> createUserWithoutGoalAndNoteNoTx(@Valid UserCompositeRequest userRequest);

    @Operation(summary = "Create user, meal and note with transaction")
    @ApiResponse(responseCode = "201", description = "Composite data created")
    @BadRequestApiResponse
    @ConflictApiResponse
    @InternalServerErrorApiResponse
    ResponseEntity<UserResponse> createUserWithGoalAndNoteTx(@Valid UserCompositeRequest userRequest);
}
