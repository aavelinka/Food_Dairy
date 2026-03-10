package com.uni.project.controller;

import com.uni.project.model.dto.request.BodyParametersRequest;
import com.uni.project.model.dto.request.NutritionalValueRequest;
import com.uni.project.model.dto.response.BodyParametersResponse;
import com.uni.project.model.dto.response.NutritionalValueResponse;
import com.uni.project.service.BodyParametersService;
import jakarta.validation.Valid;
import java.time.LocalDate;
import java.util.List;
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

@RestController
@RequestMapping("/api/body-parameters")
@AllArgsConstructor
public class BodyParametersController {
    private final BodyParametersService bodyParametersService;

    @PostMapping
    public ResponseEntity<BodyParametersResponse> bodyParametersCreate(
            @Valid @RequestBody BodyParametersRequest bodyParametersRequest) {
        return ResponseEntity.status(HttpStatus.CREATED).body(
                bodyParametersService.bodyParametersCreate(bodyParametersRequest)
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<BodyParametersResponse> getBodyParametersById(@PathVariable Integer id) {
        return ResponseEntity.ok(bodyParametersService.getBodyParametersById(id));
    }

    @GetMapping
    public ResponseEntity<List<BodyParametersResponse>> getAllBodyParameters() {
        return ResponseEntity.ok(bodyParametersService.getAllBodyParameters());
    }

    @PutMapping("/{id}")
    public ResponseEntity<BodyParametersResponse> bodyParametersUpdate(
            @PathVariable Integer id,
            @Valid @RequestBody BodyParametersRequest bodyParametersRequest) {
        return ResponseEntity.ok(bodyParametersService.bodyParametersUpdate(id, bodyParametersRequest));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> bodyParametersDelete(@PathVariable Integer id) {
        bodyParametersService.bodyParametersDelete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/user")
    public ResponseEntity<List<BodyParametersResponse>> getAllBodyParametersByUserId(
            @RequestParam("userId") Integer userId) {
        return ResponseEntity.ok(bodyParametersService.getAllBodyParametersByUserId(userId));
    }

    @GetMapping("/date")
    public ResponseEntity<List<BodyParametersResponse>> getAllBodyParametersByUserIdAndDate(
            @RequestParam("userId") Integer userId,
            @RequestParam("date") LocalDate date) {
        return ResponseEntity.ok(bodyParametersService.getAllBodyParametersByUserIdAndDate(userId, date));
    }

    @PostMapping("/user/{id}/nutritional")
    public ResponseEntity<NutritionalValueResponse> calculateNutritionalValueForUser(@PathVariable Integer id) {
        return ResponseEntity.ok(bodyParametersService.calculateNutritionalValueForUser(id));
    }

    @PutMapping("/{id}/nutritional/manual")
    public ResponseEntity<NutritionalValueResponse> setManualNutritionalValue(
            @PathVariable Integer id,
            @Valid @RequestBody NutritionalValueRequest request) {
        return ResponseEntity.ok(bodyParametersService.setManualNutritionalValue(id, request));
    }
}
