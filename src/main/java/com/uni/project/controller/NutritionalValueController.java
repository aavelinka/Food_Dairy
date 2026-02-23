package com.uni.project.controller;

import com.uni.project.model.dto.request.NutritionalValueRequest;
import com.uni.project.model.dto.response.NutritionalValueResponse;
import com.uni.project.service.NutritionalValueService;
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
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/nutritional_value")
@AllArgsConstructor
public class NutritionalValueController {
    private final NutritionalValueService nutritionalValueService;

    @GetMapping("/{id}")
    public ResponseEntity<NutritionalValueResponse> getNutritionalValueById(@PathVariable Integer id) {
        return ResponseEntity.ok(nutritionalValueService.getNutritionalValueById(id));
    }

    @GetMapping
    public ResponseEntity<List<NutritionalValueResponse>> getAllNutritionalValues() {
        return ResponseEntity.ok(nutritionalValueService.getAllNutritionalValues());
    }

    @PostMapping
    public ResponseEntity<NutritionalValueResponse> nutritionalValueCreate(
            @Valid @RequestBody NutritionalValueRequest nutritionalValueRequest) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(nutritionalValueService.nutritionalValueCreate(nutritionalValueRequest));
    }

    @PutMapping("/{id}")
    public ResponseEntity<NutritionalValueResponse> nutritionalValueUpdate(
            @PathVariable Integer id, @Valid @RequestBody NutritionalValueRequest nutritionalValueRequest) {
        return ResponseEntity.ok(nutritionalValueService.nutritionalValueUpdate(id, nutritionalValueRequest));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> nutritionalValueDelete(@PathVariable Integer id) {
        nutritionalValueService.nutritionalValueDelete(id);
        return ResponseEntity.noContent().build();
    }
}
