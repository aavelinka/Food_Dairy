package com.uni.project.controller;

import com.uni.project.controller.api.BodyParametersControllerApi;
import com.uni.project.model.dto.request.BodyParametersRequest;
import com.uni.project.model.dto.request.NutritionalValueRequest;
import com.uni.project.model.dto.response.BodyParametersResponse;
import com.uni.project.model.dto.response.NutritionalValueResponse;
import com.uni.project.service.BodyParametersService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import java.time.LocalDate;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.format.annotation.DateTimeFormat;
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

@RestController
@RequestMapping("/api/body-parameters")
@AllArgsConstructor
@Validated
public class BodyParametersController implements BodyParametersControllerApi {
    private final BodyParametersService bodyParametersService;

    @PostMapping
    public ResponseEntity<BodyParametersResponse> bodyParametersCreate(
            @Valid @RequestBody BodyParametersRequest bodyParametersRequest) {
        return ResponseEntity.status(HttpStatus.CREATED).body(
                bodyParametersService.bodyParametersCreate(bodyParametersRequest)
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<BodyParametersResponse> getBodyParametersById(@PathVariable @Positive Integer id) {
        return ResponseEntity.ok(bodyParametersService.getBodyParametersById(id));
    }

    @GetMapping
    public ResponseEntity<List<BodyParametersResponse>> getAllBodyParameters() {
        return ResponseEntity.ok(bodyParametersService.getAllBodyParameters());
    }

    @PutMapping("/{id}")
    public ResponseEntity<BodyParametersResponse> bodyParametersUpdate(
            @PathVariable @Positive Integer id,
            @Valid @RequestBody BodyParametersRequest bodyParametersRequest) {
        return ResponseEntity.ok(bodyParametersService.bodyParametersUpdate(id, bodyParametersRequest));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> bodyParametersDelete(@PathVariable @Positive Integer id) {
        bodyParametersService.bodyParametersDelete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/user")
    public ResponseEntity<List<BodyParametersResponse>> getAllBodyParametersByUserId(
            @RequestParam("userId") @Positive Integer userId
    ) {
        return ResponseEntity.ok(bodyParametersService.getAllBodyParametersByUserId(userId));
    }

    @GetMapping("/date")
    public ResponseEntity<List<BodyParametersResponse>> getAllBodyParametersByUserIdAndDate(
            @RequestParam("userId") @Positive Integer userId,
            @RequestParam("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date
    ) {
        return ResponseEntity.ok(bodyParametersService.getAllBodyParametersByUserIdAndDate(userId, date));
    }

    @PostMapping("/user/{id}/nutritional")
    public ResponseEntity<NutritionalValueResponse> calculateNutritionalValueForUser(
            @PathVariable @Positive Integer id
    ) {
        return ResponseEntity.ok(bodyParametersService.calculateNutritionalValueForUser(id));
    }

    @PutMapping("/{id}/nutritional/manual")
    public ResponseEntity<NutritionalValueResponse> setManualNutritionalValue(
            @PathVariable @Positive Integer id,
            @Valid @RequestBody NutritionalValueRequest request) {
        return ResponseEntity.ok(bodyParametersService.setManualNutritionalValue(id, request));
    }
}
