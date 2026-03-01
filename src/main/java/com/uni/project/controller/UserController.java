package com.uni.project.controller;

import com.uni.project.model.dto.request.UserCompositeRequest;
import com.uni.project.model.dto.request.UserMeasurementsRequest;
import com.uni.project.model.dto.request.UserRequest;
import com.uni.project.model.dto.response.NutritionalValueResponse;
import com.uni.project.model.dto.response.UserResponse;
import com.uni.project.model.entity.Sex;
import com.uni.project.service.UserService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
public class UserController {
    private final UserService userService;

    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> getUserById(@PathVariable Integer id){
        return ResponseEntity.ok(userService.getUserById(id));
    }

    @GetMapping
    public ResponseEntity<List<UserResponse>> getAllUsers(){
        return ResponseEntity.ok(userService.getAllUsers());
    }

    @PostMapping
    public ResponseEntity<UserResponse> userCreate(@Valid @RequestBody UserRequest userRequest){
        return ResponseEntity.status(HttpStatus.CREATED).body(userService.userCreate(userRequest));
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserResponse> userUpdate(@PathVariable Integer id,
                                                   @Valid @RequestBody UserRequest userRequest){
        return ResponseEntity.ok(userService.userUpdate(id, userRequest));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> userDelete(@PathVariable Integer id){
        userService.userDelete(id);

        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}/measurements")
    public ResponseEntity<UserResponse> measurementsUpdate(@PathVariable Integer id,
                                                           @Valid @RequestBody UserMeasurementsRequest userRequest){
        return ResponseEntity.ok(userService.measurementsUpdate(id, userRequest));
    }

    @GetMapping("/name")
    public ResponseEntity<List<UserResponse>> getAllUsersByName(@RequestParam String nameSearch) {
        return ResponseEntity.ok(userService.getAllUsersByName(nameSearch));
    }

    @GetMapping("/sex")
    public ResponseEntity<List<UserResponse>> getAllUsersBySex(@RequestParam Sex sexSearch) {
        return ResponseEntity.ok(userService.getAllUsersBySex(sexSearch));
    }

    @GetMapping("/age")
    public ResponseEntity<List<UserResponse>>  getAllUsersByAge(@RequestParam Integer ageSearch) {
        return ResponseEntity.ok(userService.getAllUsersByAge(ageSearch));
    }

    @PostMapping("/{id}/nutritional")
    public ResponseEntity<NutritionalValueResponse> calculateNutritionalValueForUser(@PathVariable Integer id) {
        return ResponseEntity.ok(userService.calculateNutritionalValueForUser(id));
    }

    @GetMapping("/with-meals")
    public ResponseEntity<List<UserResponse>> findAllWithMeals() {
        return ResponseEntity.ok(userService.findAllWithMeals());
    }

    @PostMapping("/without_transaction")
    public ResponseEntity<UserResponse> createUserWithGoalAndNoteNoTx(
            @Valid @RequestBody UserCompositeRequest userRequest) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(userService.createUserWithGoalAndNoteNoTx(userRequest));
    }

    @PostMapping("/with_transaction")
    public ResponseEntity<UserResponse> createUserWithGoalAndNoteTx(
            @Valid @RequestBody UserCompositeRequest userRequest) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(userService.createUserWithGoalAndNoteTx(userRequest));
    }
}
