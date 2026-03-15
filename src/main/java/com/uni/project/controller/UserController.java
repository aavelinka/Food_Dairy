package com.uni.project.controller;

import com.uni.project.controller.api.UserControllerApi;
import com.uni.project.model.dto.request.UserCompositeRequest;
import com.uni.project.model.dto.request.UserRequest;
import com.uni.project.model.dto.response.UserResponse;
import com.uni.project.model.entity.Sex;
import com.uni.project.service.UserService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@AllArgsConstructor
@Validated
public class UserController implements UserControllerApi {
    private final UserService userService;

    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> getUserById(@PathVariable @Positive Integer id) {
        return ResponseEntity.ok(userService.getUserById(id));
    }

    @GetMapping
    public ResponseEntity<Page<UserResponse>> getAllUsers(
            @PageableDefault(size = 10, sort = "id") Pageable pageable) {
        return ResponseEntity.ok(userService.getAllUsers(pageable));
    }

    @PostMapping
    public ResponseEntity<UserResponse> userCreate(@Valid @RequestBody UserRequest userRequest) {
        return ResponseEntity.status(HttpStatus.CREATED).body(userService.userCreate(userRequest));
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserResponse> userUpdate(
            @PathVariable @Positive Integer id,
            @Valid @RequestBody UserRequest userRequest
    ) {
        return ResponseEntity.ok(userService.userUpdate(id, userRequest));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> userDelete(@PathVariable @Positive Integer id) {
        userService.userDelete(id);

        return ResponseEntity.noContent().build();
    }

    @GetMapping("/name")
    public ResponseEntity<List<UserResponse>> getAllUsersByName(@RequestParam @NotBlank String nameSearch) {
        return ResponseEntity.ok(userService.getAllUsersByName(nameSearch));
    }

    @GetMapping("/sex")
    public ResponseEntity<List<UserResponse>> getAllUsersBySex(@RequestParam Sex sexSearch) {
        return ResponseEntity.ok(userService.getAllUsersBySex(sexSearch));
    }

    @GetMapping("/age")
    public ResponseEntity<Page<UserResponse>> getAllUsersByAge(
            @RequestParam @Positive Integer ageSearch,
            @PageableDefault(size = 10, sort = "id") Pageable pageable) {
        return ResponseEntity.ok(userService.getAllUsersByAge(ageSearch, pageable));
    }

    @GetMapping("/age/native")
    public ResponseEntity<Page<UserResponse>> getAllUsersByAgeNative(
            @RequestParam @Positive Integer ageSearch,
            @PageableDefault(size = 10, sort = "id") Pageable pageable) {
        return ResponseEntity.ok(userService.getAllUsersByAgeNative(ageSearch, pageable));
    }

    @GetMapping("/with-meals")
    public ResponseEntity<List<UserResponse>> findAllWithMealsAndBodyParameters() {
        return ResponseEntity.ok(userService.findAllWithMealsAndBodyParameters());
    }

    @PostMapping("/without_transaction")
    public ResponseEntity<UserResponse> createUserWithoutGoalAndNoteNoTx(
            @Valid @RequestBody UserCompositeRequest userRequest) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(userService.createUserWithoutGoalAndNoteNoTx(userRequest));
    }

    @PostMapping("/with_transaction")
    public ResponseEntity<UserResponse> createUserWithGoalAndNoteTx(
            @Valid @RequestBody UserCompositeRequest userRequest) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(userService.createUserWithGoalAndNoteTx(userRequest));
    }
}
