package com.uni.project.controller;

import com.uni.project.model.dto.request.WaterIntakeRequest;
import com.uni.project.model.dto.response.WaterIntakeResponse;
import com.uni.project.service.WaterIntakeService;
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
@RequestMapping("/api/water-intakes")
@AllArgsConstructor
public class WaterIntakeController {
    private final WaterIntakeService waterIntakeService;

    @PostMapping
    public ResponseEntity<WaterIntakeResponse> waterIntakeCreate(
            @Valid @RequestBody WaterIntakeRequest waterIntakeRequest) {
        return ResponseEntity.status(HttpStatus.CREATED).body(waterIntakeService.waterIntakeCreate(waterIntakeRequest));
    }

    @GetMapping("/{id}")
    public ResponseEntity<WaterIntakeResponse> getWaterIntakeById(@PathVariable Integer id) {
        return ResponseEntity.ok(waterIntakeService.getWaterIntakeById(id));
    }

    @GetMapping
    public ResponseEntity<List<WaterIntakeResponse>> getAllWaterIntakes() {
        return ResponseEntity.ok(waterIntakeService.getAllWaterIntakes());
    }

    @PutMapping("/{id}")
    public ResponseEntity<WaterIntakeResponse> waterIntakeUpdate(@PathVariable Integer id,
                                                                 @Valid @RequestBody
                                                                 WaterIntakeRequest waterIntakeRequest) {
        return ResponseEntity.ok(waterIntakeService.waterIntakeUpdate(id, waterIntakeRequest));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> waterIntakeDelete(@PathVariable Integer id) {
        waterIntakeService.waterIntakeDelete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/user")
    public ResponseEntity<List<WaterIntakeResponse>> getAllWaterIntakesByUserId(
            @RequestParam("userId") Integer userId) {
        return ResponseEntity.ok(waterIntakeService.getAllWaterIntakesByUserId(userId));
    }

    @GetMapping("/date")
    public ResponseEntity<List<WaterIntakeResponse>> getAllWaterIntakesByDate(
            @RequestParam("userId") Integer userId,
            @RequestParam("date") LocalDate date) {
        return ResponseEntity.ok(waterIntakeService.getAllWaterIntakesByUserIdAndDate(userId, date));
    }

    @GetMapping("/daily-total")
    public ResponseEntity<Integer> getDailyTotalByUserIdAndDate(
            @RequestParam("userId") Integer userId,
            @RequestParam("date") LocalDate date) {
        return ResponseEntity.ok(waterIntakeService.getDailyTotalByUserIdAndDate(userId, date));
    }
}
