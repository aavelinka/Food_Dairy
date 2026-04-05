package com.uni.project.controller;

import com.uni.project.controller.api.DemoControllerApi;
import com.uni.project.model.dto.response.RaceConditionDemoResponse;
import com.uni.project.service.RaceConditionDemoService;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/demo")
@AllArgsConstructor
@Validated
public class DemoController implements DemoControllerApi {
    private final RaceConditionDemoService raceConditionDemoService;

    @GetMapping("/race-condition/unsafe")
    public ResponseEntity<RaceConditionDemoResponse> runUnsafeRaceConditionDemo(
            @RequestParam(defaultValue = "50") @Min(50) Integer threadCount,
            @RequestParam(defaultValue = "1000") @Positive Integer incrementsPerThread
    ) {
        return ResponseEntity.ok(raceConditionDemoService.runUnsafeDemo(threadCount, incrementsPerThread));
    }

    @GetMapping("/race-condition/atomic")
    public ResponseEntity<RaceConditionDemoResponse> runAtomicRaceConditionDemo(
            @RequestParam(defaultValue = "50") @Min(50) Integer threadCount,
            @RequestParam(defaultValue = "1000") @Positive Integer incrementsPerThread
    ) {
        return ResponseEntity.ok(raceConditionDemoService.runAtomicDemo(threadCount, incrementsPerThread));
    }
}
